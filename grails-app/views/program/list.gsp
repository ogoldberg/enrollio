<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<!-- This template is to use when creating new blank gsps -->
<html xmlns="http://www.w3.org/1999/xhtml">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <title>Something</title>
    </head>
    <body>
        <div id="top-panel">
            <div id="panel">
                <ul>
                    <li>
                        <g:link action="create" class="addorder">New Program</g:link>
                    </li>
                </ul>
            </div>
        </div>
        <div id="wrapper">
            <div id="content">
                <div id="rightnow">
                    <h3 class="reallynow">
                        <span>Programs</span>
                        <br />
                    </h3>
                    <table>
                        <thead></thead>
                        <tbody>
                            <g:each in="${programInstanceList}" status="i"
                            var="programInstance">
                                <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                                    <td>
                                        <g:link action="show" id="${programInstance.id}">
                                        ${fieldValue(bean:programInstance,
                                        field:'name')}</g:link>
                                    </td>
                                    <td>${fieldValue(bean:programInstance,
                                    field:'description')}</td>
                                </tr>
                            </g:each>
                        </tbody>
                    </table>
                </div>
            </div>
            <div id="sidebar">
                <g:render template="/common/sideMenu" />
            </div>
        </div>
    </body>
</html>
