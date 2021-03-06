<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
  "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html>
    <head>
        <title>
            <g:layoutTitle default="Enrollio" />
        </title>
        <link rel="stylesheet" type="text/css" href="${resource(dir:'css/redmond', file:'jquery-ui-1.7.2.custom.css')}" />
        <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
        <!--[if IE]>
        <link rel="stylesheet" type="text/css" href="css/ie-sucks.css" />
        <![endif]-->
        <link rel="stylesheet" type="text/css" href="${resource(dir:'css', file:'theme.css')}" />
        <link rel="stylesheet" type="text/css" href="${resource(dir:'css', file:'style.css')}" />
        <link rel="stylesheet" type="text/css" href="${resource(dir:'css', file:'theme1.css')}" />
        <g:layoutHead />
        <g:javascript library="application" />
    </head>
    <body>
        <div id="container">
            <div id="header">
                <g:mascotIcon style="vertical-align:middle" />
                <shiro:isLoggedIn>
                    <h2 style="display:inline">Welcome to Enrollio, 
                    <shiro:principal />!</h2>
                </shiro:isLoggedIn>
                <shiro:isNotLoggedIn>
                    <h2 style="display:inline">Welcome to Enrollio!</h2>
                </shiro:isNotLoggedIn>
                <div id="topmenu">
                    <ul>
                        <li class="${isCurrentTab(tabName:'program')}">
                            <g:link controller="program" action="list">Programs</g:link>
                        </li>
                        <li class="${isCurrentTab(tabName:'classSession')}">
                            <g:link controller="classSession" action="list"
                            class="enr-top-menu-item">Class Sessions</g:link>
                        </li>
                        <li class="${isCurrentTab(tabName:'contact')}">
                            <g:link controller="contact" action="list">Contacts</g:link>
                        </li>
                        <li class="${isCurrentTab(tabName:'student')}">
                            <g:link controller="student" action="list"
                            class="enr-top-menu-item">Students</g:link>
                        </li>
                        <shiro:hasRole name="Administrator">
                            <li class="${isCurrentTab(tabName:'admin')}">
                                <g:link controller="admin" class="enr-top-menu-item">
                                Admin</g:link>
                            </li>
                        </shiro:hasRole>
                        <li class="${isCurrentTab(tabName:'help')}">
                            <g:link controller="help" action="index"
                            class="enr-top-menu-item">Help</g:link>
                        </li>
                        <shiro:isNotLoggedIn>
                            <li class="${isLoginTab(tabName:'login')}">
                                <g:link controller="auth" action="login">Login</g:link>
                            </li>
                        </shiro:isNotLoggedIn>
                        <shiro:isLoggedIn>
                            <li class="logintab">
                                <g:link controller="auth" action="signOut">Logout</g:link>
                            </li>
                        </shiro:isLoggedIn>
                    </ul>
                </div>
            </div>
            <g:layoutBody />
        </div>
    </body>
</html>
