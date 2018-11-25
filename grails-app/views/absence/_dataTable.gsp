<el:dataTable id="absenceTable"
              searchFormName="absenceSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="absence"
              spaceBefore="true"
              hasRow="true"
              action="filter"
              serviceName="absence"
              domainColumns="${domainColumns}">


    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'absence', action: 'show')}"
                            actionParams="encodedId"  functionName="renderInLineShow" type="function" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity],
                                    default: 'show absence')}"/>
    </g:if>

    <g:else>
        <el:dataTableAction controller="absence" action="show"
                            actionParams="encodedId" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity],
                                    default: 'show absence')}"/>

        <el:dataTableAction controller="absence" action="edit"
                            actionParams="encodedId" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label',
                                    args: [entity], default: 'edit absence')}"/>

        <el:dataTableAction controller="absence" action="delete"
                            actionParams="encodedId" class="red icon-trash" type="confirm-delete"
                            message="${message(code: 'default.delete.label', args: [entity],
                                    default: 'delete absence')}"/>
    </g:else>
</el:dataTable>