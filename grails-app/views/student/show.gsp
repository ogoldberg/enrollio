
<%@ page import="org.bworks.bworksdb.Student" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="main" />
        <meta name="tabName" content="student" />
        <title>Student: ${studentInstance}</title>
    </head>
    <body>
        <div id="wrapper">
            <div id="content">
            <g:if test="${flash.message}">
                <div class="message">${flash.message}</div>
            </g:if>
            <div class="box">
                <h3>Student: ${studentInstance}</h3>
                <table>
                    <tbody>
                        <tr class="prop">
                            <td valign="top" class="name">Contact:</td>
                            
                            <td valign="top" class="value">
                                <g:link controller="contact"
                                          name="contactLink"
                                          action="show"
                                          id="${studentInstance.contact?.id}">
                                          ${studentInstance.contact}
                                </g:link>
                            
                        </tr>
                            
                        <tr class="prop">
                            <td valign="top" class="name">Birth Date:</td>
                            
                            <td valign="top" class="value">
                                <enrollio:formatDate date="${studentInstance.birthDate}" />
                            </td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name">Grade:</td>
                            
                            <td valign="top" class="value">${fieldValue(bean:studentInstance, field:'grade')}</td>
                            
                        </tr>
                        <tr class="prop">
                            <td valign="top" class="name">Gender:</td>
                            
                            <td valign="top" class="value">${fieldValue(bean:studentInstance, field:'gender')}</td>
                            
                        </tr>
                        <tr class="prop">
                            <td valign="top" class="name">Email Address:</td>
                            
                            <td valign="top" class="value">${fieldValue(bean:studentInstance, field:'emailAddress')}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name">Interests:</td>
                            
                            <td  valign="top" style="text-align:left;" class="value">
                                <g:activeInterestLinks student="${studentInstance}"/>
                            </td>
                            
                        </tr>
                    
                    
                    </tbody>
                </table>
            </div>
        </div>
        </div><div id="sidebar">
            <g:render template="studentMenu" />
        </div>
        </div>
    </body>
</html>
