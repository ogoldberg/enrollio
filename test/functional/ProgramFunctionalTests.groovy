class ProgramFunctionalTests extends functionaltestplugin.FunctionalTestCase {
    // TODO loginAs should be refactored into a
    // common method -- it's also used in SecurityFiltersFunctionalTests
    void loginAs(userName, pass) {
        get('/login')
        form('loginForm') {
            username = userName
            password = pass
            click "login"
        }
    }

    void testProgramList() {
        loginAs('bob', 'bobbobbob0')
        click("Programs")
        assertStatus 200
        assertTitleContains("Programs")
        assertContentContains("Children's EAC")
    }

    // Ensure that we can create a new Program
    void testProgramMenu() {
        loginAs('bob', 'bobbobbob0')
        click("Programs")
        assertStatus 200
        assertNotNull byId('programMenu')

    }

    void testNewProgram() {
        loginAs('bob', 'bobbobbob0')
        click("Programs")
        assertStatus 200
        click("newProgramLink")
        assertStatus 200
        assertTitleContains("Create Program")
    }

    void testCallList() {
        loginAs('bob', 'bobbobbob0')
        click("Programs")
        assertStatus 200
        // find first a href that starts with programLink
        // -- it's a link to a Program.
        def progLink = byXPath("//a[starts-with(@name,'programLink')]")
        assertNotNull progLink
        // click("newProgramLink")
        // assertStatus 200
        // assertTitleContains("Create Program")
    }
}
