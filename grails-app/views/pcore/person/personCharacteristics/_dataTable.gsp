
<el:dataTable id="personCharacteristicsTable"
              searchFormName="personCharacteristicsSearchForm"

              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12"
              controller="personCharacteristics" spaceBefore="true"
              hasRow="true" action="filter"
              serviceName="personCharacteristics"
              domainColumns="${domainColumns}">


    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'personCharacteristics',action: 'show')}" functionName="renderInLineShow" type="function" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show personCharacteristics')}"/>
        <el:dataTableAction accessUrl="${createLink(controller: 'personCharacteristics',action: 'edit')}" functionName="renderInLineEdit" type="function" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity], default: 'edit personCharacteristics')}"/>

    </g:if>
    <g:else>
        <el:dataTableAction controller="personCharacteristics" action="show" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show personCharacteristics')}"/>
        <el:dataTableAction controller="personCharacteristics" action="edit" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity],default: 'edit personCharacteristics')}"/>
    </g:else>
    <el:dataTableAction controller="personCharacteristics" action="delete" class="red icon-trash" type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete personCharacteristics')}"/>
</el:dataTable>
