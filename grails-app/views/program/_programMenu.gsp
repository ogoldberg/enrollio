<ul name="programMenu">
    <li>
        <h3>
            <g:link class="modules" controller="program" action="list">Programs</g:link>
        </h3>
        <ul>
            <li>
                <g:link url="${[controller:'program', action:'create']}"
                         id="newProgramLink"
                      class="module_add">&nbsp;New</g:link>
            </li>
            <g:if test="${programInstance}">
                <li>
                    <g:link class="module_edit" controller="program" action="edit" id="${programInstance.id}">Edit</g:link>
                </li>
            </g:if>
        </ul>
    </li>
    <g:if test="${programInstance}">
    <ul>
    <li>
        <h3>
            <a href="#" class="module">${programInstance}</a>
        </h3>
        </li>
    </ul>
    </g:if>
</ul>
<g:if test="${programInstance}">
    <ul>
        <li>
          <script type="text/javascript">
                function submit_callList(link) {
                  link.parentNode._format.value = link.title;
                  link.parentNode.submit();
                  return false;
                }
          </script>
        <g:form name="callList" 
               class="jasperReport" 
               action="callList">
                <input type="hidden" name="_format" value="PDF" />
                <!-- Name shown on top of PDF report -->
                <input type="hidden" name="_name" value="Call List" />
                <input type="hidden" name="_file" value="callList" />
                <input type="hidden" name="id" value="${programInstance.id}" />
                <!-- TODO The &nbsp; is a kludge find CSS way to justify image
                and text so it looks o.k. -->
                <a href="#" name="callListLink" class="telephone" title="PDF" onClick="return submit_callList(this)">
                &nbsp;&nbsp;Call List
                </a>
        </g:form>
        </li>
    </ul>
</g:if>
