<el:dataTable id="profileNoteTable" searchFormName="profileNoteSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="profileNote"
              spaceBefore="true" hasRow="true" action="filter" serviceName="profileNote"
              domainColumns="${domainColumns}">



    <g:if test="${isInLineActions}">

        <el:dataTableAction accessUrl="${createLink(controller: 'profileNote',
                action: 'show')}" functionName="renderInLineShow" type="function"
                            actionParams="encodedId" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity],
                                    default: 'show profileNote')}" />


        <el:dataTableAction accessUrl="${createLink(controller: 'profileNote',
                action: 'edit')}" functionName="renderInLineEdit" type="function"
                            actionParams="encodedId"  class="blue icon-pencil"
                            message="${message(code: 'default.edit.label',
                                    args: [entity], default: 'edit profileNote')}"/>

    </g:if>
    <g:else>
        <el:dataTableAction controller="profileNote" action="show"
                            actionParams="encodedId" class="green icon-eye"
                            message="${message(code:'default.show.label',args:[entity],default:'show profileNote')}" />

        <el:dataTableAction controller="profileNote" action="edit"
                            actionParams="encodedId" class="blue icon-pencil"
                            message="${message(code:'default.edit.label',args:[entity],default:'edit profileNote')}" />


    </g:else>


    <el:dataTableAction controller="profileNote"
                        action="delete" actionParams="encodedId"
                        class="red icon-trash" type="confirm-delete"
                        message="${message(code:'default.delete.label',args:[entity],default:'delete profileNote')}" />




</el:dataTable>