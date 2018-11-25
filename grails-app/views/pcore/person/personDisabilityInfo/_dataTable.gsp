

<el:dataTable id="personDisabilityInfoTable" searchFormName="personDisabilityInfoSearchForm"

              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12"
              controller="personDisabilityInfo" spaceBefore="true"
              hasRow="true" action="filter"
              serviceName="personDisabilityInfo"
              domainColumns="${domainColumns}">

    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'personDisabilityInfo',action: 'show')}" functionName="renderInLineShow" type="function" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show personDisabilityInfo')}"/>
        <el:dataTableAction accessUrl="${createLink(controller: 'personDisabilityInfo',action: 'edit')}" functionName="renderInLineEdit" type="function" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity], default: 'edit personDisabilityInfo')}"/>

    </g:if>
    <g:else>
        <el:dataTableAction controller="personDisabilityInfo" action="show" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show personDisabilityInfo')}"/>
        <el:dataTableAction controller="personDisabilityInfo" action="edit" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity],default: 'edit personDisabilityInfo')}"/>
    </g:else>
    <el:dataTableAction controller="personDisabilityInfo" action="delete" class="red icon-trash" type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete personDisabilityInfo')}"/>
</el:dataTable>

