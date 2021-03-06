package org.bworks.bworksdb
import grails.test.*

class ProgramService {

    boolean transactional = true

    def sequenceIncr = 100

    // Go through Programs's Lessons, and create dummy
    // LessonDates, starting with startDate
    def nextAvailableLessonDates(Program p, Date startDate) {
        def d = startDate
        def proposedClasses = []
        p.lessons.each {
            proposedClasses << new LessonDate(lesson:it, lessonDate:d)
            d = d + 7
        }

        return proposedClasses
    }

    def getCallList(id) {
        def prog = Program.get(id)
        if (!prog) return null;
        def interests = prog.interests.findAll { it.active == true }
        def students = interests.collect { it.student }
        def contacts = students.collect { it.contact }
    }

    def activeInterests(Program p) {
        return Interest.findAllByProgramAndActive(p, true)
    }


    // Utility method to make sure we have lesson sequences
    // in a standard order (separated by sequenceIncr, so that we can easily
    // add new Lessons between other lessons w/o trampling existing sequences)
    def sequenceLessons(Program p) {
        p.refresh()
        def lessons = p.lessons.collect { it }
        def newSequence = sequenceIncr
        lessons.each {
            if (it.sequence != newSequence) {
                it.sequence = newSequence
                it.save(flush:true)
            }
            newSequence += sequenceIncr
        }
    }

    def sortLessons(Program p, params) {
        def seq = sequenceIncr
        def lessonIdList = sortedLessonIdList(params)
        lessonIdList.each {
            def lesson = Lesson.get(it)
            if ( lesson.sequence != seq ) {
                lesson.sequence = seq
                lesson.save(flush:true)
            }
            seq = seq + sequenceIncr
            
        }
        // clean up any lessons that might have been saved
        // since the lessonIdList was composed
        sequenceLessons(p)
    }

    // returns a list of lesson IDs, sorted by their
    // suggested sequence
    // Takes a map consisting of data like this:
    // [ 
    //     'lessonId_42' : 1,
    //     'lessonId_64' : 2,
    //     'lessonId_1'  : 4,
    //     'lessonId_76' : 3,
    //     'lessonId_2'  : '16' 
    // ]
    //  And returns this : [ 42, 64, 76, 1, 2 ]
    def sortedLessonIdList(params) {
        def lessonMap = params.findAll { 
            it.key =~ /lessonId_/
        }

        def sortedLessonIds = lessonMap.keySet().sort {
            lessonMap[it].toInteger()
        } 

        // Rip the xx from 'lessonId_xx'
        sortedLessonIds = sortedLessonIds.collect {
            it.split('_')[1].toInteger()
        }

        return sortedLessonIds
        
    }

    def nextAvailSequence(Program p) {
        def lesson
        try {
            lesson = p.lessons?.last()
        } catch (Exception e) {
            // for some reason, programs with no
            // lessons get here in the functional tests,
            // but not integration tests.  See testNextAvailSequence -
            // it doesn't fail, but testNewLessonForProgram does. :-/
        }
        return lesson ? lesson.sequence + sequenceIncr : sequenceIncr
    }

}
