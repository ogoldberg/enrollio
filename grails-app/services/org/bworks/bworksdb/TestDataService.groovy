package org.bworks.bworksdb

import org.bworks.bworksdb.auth.*
import org.apache.shiro.crypto.hash.Sha1Hash
import org.bworks.bworksdb.util.TestKeys
import org.codehaus.groovy.grails.commons.*



class TestDataService {

    boolean transactional = true

    def programService
    def config = ConfigurationHolder.config


        
    // we don't want random data for integration tests
    def loadIntegrationTestData() {
        // get programs
        loadDefaultPrograms()
                
        // build contact, student
        def contact = new Contact(firstName:TestKeys.CONTACT1_FIRST_NAME,
                            lastName:TestKeys.CONTACT1_LAST_NAME,
                            address1:'add1',
                            address2:'add2',
                            city:'Saint Louis',
                            state:'MO',
                            zipCode:'63043',
                            emailAddress:TestKeys.CONTACT_EMAIL).save()

        def student = new Student(lastName:TestKeys.STUDENT, contact:contact)
        def student2 = new Student(lastName:TestKeys.STUDENT2, contact:contact)

        contact.addToStudents(student)
        contact.addToStudents(student2)
        contact.save(flush:true)
        
        // interests
        addInterest(student, Program.findByName(TestKeys.PROGRAM_ADULT_AEC), true)

        addInterest(student, Program.findByName(TestKeys.PROGRAM_KIDS_AEC), false)
        addInterest(student2, Program.findByName(TestKeys.PROGRAM_KIDS_AEC), true)

        loadDummyRegularUser()
        // load dummy class sessions for now -- we should
        // create more predictable test data for integration tests
        loadDummyClassSessions()
    }

    def loadDummyRegularUser() {

        // Administrator user and role.
        def userRole = ShiroRole.findByName("User")
        def user = new ShiroUser(username: "bob",
            firstName : 'Bob',
            lastName : 'Dog',
            password : 'bobbobbob0',
            passwordConfirm : 'bobbobbob0',
            passwordHash: new Sha1Hash("bobbobbob0").toHex()
        )
        if (!user.validate()) {
            println "User didn't validate!"
            println user.errors.allErrors
        }
        else {
            user.save()
            new ShiroUserRoleRel(user: user, role: userRole).save()
        }

    }

    def addInterest(student, program, isActive) {
        // add interest to program and student
        def note = new Note(text:TestKeys.NOTE).save()
        def interest = new Interest(active:isActive, student:student, program:program, note:note).save()        
        program.addToInterests(interest)
        student.addToInterests(interest)        

        student.save(flush:true)
        program.save(flush:true)
    }
    
    // Git some test data in these here parts
    def loadDevData(numContacts = 100) {
        loadDefaultPrograms()
        loadDummyRegularUser()
 
        numContacts.times {
            loadDummyContactAndStudents()
        }
        loadDummyClassSessions()
        loadDummyUsers()
        loadDefaultConfigSettings()
    } 

    def loadDummyClassSessions() {
        // define start dates for the various test programs in a hash
        // for easy assignment
        def testProgs = [:]

        testProgs[TestKeys.PROGRAM_MENTORSHIP] = [ 'date' : TestKeys.SESSION_MENTORSHIP_DATE,
                                                   'sessionName' : TestKeys.SESSION_MENTORSHIP_NAME ]

        testProgs[TestKeys.PROGRAM_ADULT_AEC]  = [ 'date' : TestKeys.SESSION_ADULT_DATE  ,
                                                   'sessionName' : TestKeys.SESSION_ADULT_NAME ]

        testProgs[TestKeys.PROGRAM_KIDS_AEC]   = [ 'date' : TestKeys.SESSION_KIDS_DATE,
                                                   'sessionName' : TestKeys.SESSION_KIDS_NAME ]

        testProgs.each { key, testProg ->
            def p = Program.findByName(key)
            def classSession = new ClassSession(name:testProg.sessionName,
                                      program:p,
                                      startDate: testProg.date).save()
            
            def nextLessonDates = 
                programService.nextAvailableLessonDates(classSession.program, new Date())

            nextLessonDates.each { lessonDate ->
                classSession.addToLessonDates(lessonDate)
            }

            classSession.save()

            // Enroll Student in this new Session
            p.interests.eachWithIndex  { interest, i ->
                if (i < 5) {
                    classSession.addToEnrollments(new Enrollment(student:interest.student))
                }
                else {
                    return
                }
            }
            classSession.save()
        }

   }

    def loadDummyUsers(numUsers = 3) {
        numUsers.times {
            def userRole = ShiroRole.findByName("User")
            def lastName = randomLastName()
            def firstName = randomFirstName()
            def userName = firstName.substring(0,1) + lastName 
            
            def password = "${firstName}0"
            if (password.length() != 5) { password = "${firstName}${firstName}0" }
            
            def dummyUser = new ShiroUser(username: userName,
                    firstName : firstName,
                    lastName : lastName,
                    password : password,
                    passwordConfirm : password,
                    passwordHash: new Sha1Hash(password as String).toHex()
            )
            if (!dummyUser.validate()) {
                println "username : ${userName}"
                println "User didn't validate!"
                println adminUser.errors.allErrors
            }
            else {
                dummyUser.save()
                new ShiroUserRoleRel(user: dummyUser, role: userRole).save()
            }
        }
    }

    def loadDefaultConfigSettings() {

        if (! ConfigSetting.findByConfigKeyAndIsDefault('mascotIcon', true)) {

            def s0 = new ConfigSetting(configKey:'mascotIcon',
                                       // value:servletContext.getRealPath("/images/mascot.png"),
                                       value:config.grails.serverURL + '/images/mascot.png',
                                       isDefault: true,
                                       description:'Enrollio Mascot Icon Used on every page').save()
        }
    }

    def loadDefaultPrograms() {
        def p0 = new Program(description:"Byteworks Children's Earn-A-Computer Program",
                              name:TestKeys.PROGRAM_KIDS_AEC).save()

        // Define sample lessons.  Use a hard-coded description for
        // the test Earn-A-Computer lesson.
        def eacLessons = [ 
           [ name: TestKeys.LESSON_KIDS_AEC_INTRO, desc: TestKeys.LESSON_KIDS_AEC_INTRO_DESCRIPTION],
           [ name: 'Scratch Programming' ], 
           [ name: 'Word Processing' ], 
           [ name: 'Presentations' ],
           [ name: 'Email and WWW' ],
           [ name: 'Graduation' ]
        ]

        eacLessons.eachWithIndex { it, i ->
            if (!it.desc) { it.desc = it.name + "\n\nA description of " + it.name }
            p0.addToLessons(new Lesson(description:it.desc,
                                       name:it.name,
                                       sequence:programService.nextAvailSequence(p0)))
        }
            
        new Program(description:"Byteworks Adult Earn-A-Computer Program", name:TestKeys.PROGRAM_ADULT_AEC).save()
        new Program(description:"Byteworks Mentorship Program", name:TestKeys.PROGRAM_MENTORSHIP).save()
 
        def s0 = new ConfigSetting(configKey:'defaultInterestProgram',
                                   value:1,
                                   isDefault: true,
                                   description:'When entering Students, this program will be the default program they\'re interested in').save()
    }

    def loadDummyContacts(numContacts = 100) {

        numContacts.times {
            loadDummyContactAndStudents()
        }
    }
 
    // Method used to create a dummy contact, student and an interest
    // in a program
    def loadDummyContactAndStudents() {
        def seed = new Random()
        def randAddress = seed.nextInt(1000) 
        def address2 = ''
 
        if (randAddress.mod(3) == 0) {
            address2 = 'Apt. A'
        }
        def zip = '63' + seed.nextInt(100).toString().padLeft(3, "0") + '-1234'
        def lastName = randomLastName()
        def firstName = randomFirstName()
        // Use an email address for 50% of the people
        def emailAddress = seed.nextInt(2) == 1 ? '' : "${firstName}.${lastName}@" + randomEmailServer() + ".com"
        def c0 = new Contact(firstName:firstName,
                            lastName:lastName,
                            address1:randAddress.toString() + ' ' + randomStreetName(),
                            address2:address2,
                            city:'Saint Louis',
                            state:'MO',
                            zipCode:zip,
                            emailAddress:emailAddress).save()
 
        def randPhone = seed.nextInt(10000) - 1
        randPhone = randPhone.toString().padLeft(4, "0")
        c0.addToPhoneNumbers(new PhoneNumber(label:'Home', 
                                             phoneNumber:"(314)-123-$randPhone"))
 
        randPhone = seed.nextInt(10000) - 1
        randPhone = randPhone.toString().padLeft(4, "0")
        c0.addToPhoneNumbers(new PhoneNumber(label:'Work', 
                                             phoneNumber:"(314)-444-${randPhone}"))
 
        def numStudents = seed.nextInt(3) + 1
        numStudents.times {
            def stud = new Student(lastName:lastName,
                                    firstName:randomFirstName())
 
            c0.addToStudents(stud)
            c0.save(flush:true)
           
            // Add a random amount of interests to random programs :-)
            def programs = Program.findAll().collect { it.id }
            def availProgs = programs.clone()
            // Get 0 .. numPrograms -- some students might not have interests
            def numProgsForStudent = seed.nextInt(programs.size() + 1)
            numProgsForStudent.times {
                
                def randomProg = seed.nextInt(availProgs.size())
               
                def prog = availProgs.remove(randomProg)
                stud.addToInterests(new Interest(program:Program.get(prog), active:true))
            }
        }
 
    }
 
    def randomFirstName() {
        def seed = new Random()
        def names = ['Bob', 'Jane', 'Dooge', 'Patty', 'Charlie', 'Snoopy', 'Woodstock', 
                     'Schizoid', 'Nate', 'Dan', 'Mary', 'Adam', 'Chris', 'Theresa', 
                     'Snarf', 'Peter', 'Missie', 'Julie', 'Wong', 'Debbie']
        return names[seed.nextInt(names.size() - 1)]
    }
    
    def randomEmailServer() {
        def seed = new Random()
        def names = ['yahoo', 'aol', 'gmail', 'grails', 'fiddlesticks']
        return names[seed.nextInt(names.size() - 1)]
    }
 
    def randomLastName() {
        def seed = new Random()
        def names = ['Neff', 'James', 'Jones', 'Ryholight', 'Klein', 'Jackson',
                     'Birdcage', 'Lionheart', 'Braveheart', 'Gibson', 'Snippet',
                     'Black', 'Caldwell', 'Spacely', 'Jetson', 'Flintstone', 'Fudd',
                     'Zapata', 'Wolfenstein', 'Davis', 'Washington', 'Jefferson', 'Madison',
                     'Pujols', 'La Russa', 'Dempster', 'Ryan', 'Ramirez', 'Gonzalez',
                     'Manson', 'Dahmer', 'Link', 'Stephanopolis', 'Rather', 'Walters',
                     'Klez', 'Klockenhammer', 'Gosling', 'Torvalds', 'Stallman']
        return names[seed.nextInt(names.size() - 1)]
    }
 
    def randomStreetName() {
        def seed = new Random()
        def names = ['Neff', 'James', 'Jones', 'Ryholight', 'Klein', 'Jackson', 'Main',
                     'Oklahoma', 'Virginia', 'Royal', 'Arsenal Boulevard', 'Zipenstein', 'McCausland Avenue']
        return names[seed.nextInt(names.size() - 1)]
    }

    // Utility method to zap data loaded by
    // loadIntegrationTestData()
    def deleteIntegrationTestData() {
        // student stuff
        Enrollment.list()*.delete(flush:true)
        Student.list()*.delete(flush:true)
        LessonDate.list()*.delete(flush:true)
        ClassSession.list()*.delete(flush:true)
        // ConfigSetting.groovy
        Contact.list()*.delete(flush:true)
        // Interest.groovy
        Lesson.list()*.delete(flush:true)
        // Note.groovy
        // PhoneNumber.groovy
        Program.list()*.delete(flush:true)
        // Attendance.list()*.delete(flush:true)
        def u = ShiroUser.findByUsername('bob')
        ShiroUserRoleRel.findByUser(u).delete()
        u.delete(flush:true)
    }

}
