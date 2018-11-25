<el:dataTable id="interviewTable" searchFormName="interviewSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="interview" spaceBefore="true" hasRow="true"
              action="filter" serviceName="interview">

    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'interview', action: 'show')}"
                            functionName="renderInLineShow" type="function" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show interview')}"/>
    </g:if>
    <g:else>
        <el:dataTableAction controller="interview" action="show" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show interview')}"/>

        <el:dataTableAction controller="interview" action="edit" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity], default: 'edit interview')}"/>

        <el:dataTableAction controller="interview" action="delete" class="red icon-trash" type="confirm-delete"
                            message="${message(code: 'default.delete.label', args: [entity], default: 'delete interview')}"/>
    </g:else>
</el:dataTable>
</body>
</html>