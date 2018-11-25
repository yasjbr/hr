<el:dataTable id="secondmentNoticeTable"
              searchFormName="secondmentNoticeSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="secondmentNotice"
              spaceBefore="true"
              hasRow="true"
              action="filter"
              serviceName="secondmentNotice"
              domainColumns="${domainColumns}">


    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'secondmentNotice', action: 'show')}"
                            actionParams="encodedId"  functionName="renderInLineShow" type="function" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity],
                                    default: 'show secondmentNotice')}"/>
    </g:if>

    <g:else>
        <el:dataTableAction controller="secondmentNotice" action="show"
                            actionParams="encodedId" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity],
                                    default: 'show secondmentNotice')}"/>

        <el:dataTableAction controller="secondmentNotice" action="edit"
                            actionParams="encodedId" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label',
                                    args: [entity], default: 'edit secondmentNotice')}"/>

        <el:dataTableAction controller="secondmentNotice" action="delete"
                            actionParams="encodedId" class="red icon-trash" type="confirm-delete"
                            message="${message(code: 'default.delete.label', args: [entity],
                                    default: 'delete secondmentNotice')}"/>
    </g:else>
</el:dataTable>