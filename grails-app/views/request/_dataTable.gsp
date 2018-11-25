<el:dataTable id="requestTable"
              searchFormName="requestSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="request"
              spaceBefore="true" hasRow="true"
              action="filter"
              serviceName="request"
              domainColumns="${domainColumns}">


    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'request',action: 'show')}"
                            actionParams="encodedId" functionName="renderInLineShow" type="function" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity],
                                    default: 'show request')}"/>
    </g:if>
    <g:else>



    <el:dataTableAction controller="request" action="show"
                        actionParams="encodedId" class="green icon-eye"
                        message="${message(code:'default.show.label',args:[entity],
                                default:'show request')}" />

    <el:dataTableAction controller="request" action="edit"
                        actionParams="encodedId" class="blue icon-pencil"
                        message="${message(code:'default.edit.label',
                                args:[entity],default:'edit request')}" />

    <el:dataTableAction controller="request" action="delete"
                        actionParams="encodedId" class="red icon-trash" type="confirm-delete"
                        message="${message(code:'default.delete.label',args:[entity],
                                default:'delete request')}" />
    </g:else>

</el:dataTable>