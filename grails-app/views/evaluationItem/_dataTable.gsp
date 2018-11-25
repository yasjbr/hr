<el:dataTable id="evaluationItemTable" searchFormName="evaluationItemSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="evaluationItem" spaceBefore="true" hasRow="true" action="filter" serviceName="evaluationItem">
    <g:if test="${isInLineActions}">

        <el:dataTableAction accessUrl="${createLink(controller: 'evaluationItem', action: 'show')}"
                            functionName="renderInLineShow" type="function" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show evaluationItem')}"/>


        <el:dataTableAction accessUrl="${createLink(controller: 'evaluationItem', action: 'edit')}"
                            functionName="renderInLineEdit" type="function" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity], default: 'edit evaluationItem')}"/>

        <el:dataTableAction controller="evaluationItem" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete"
                            message="${message(code: 'default.delete.label', args: [entity], default: 'delete evaluationItem')}"/>

    </g:if>
    <g:else>
        <el:dataTableAction controller="evaluationItem" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show evaluationItem')}" />
        <el:dataTableAction controller="evaluationItem" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit evaluationItem')}" />
        <el:dataTableAction controller="evaluationItem" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete evaluationItem')}" />
    </g:else>
</el:dataTable>

<div class="clearfix form-actions text-center" style="background:gainsboro;">
<el:modalLink
        link="${createLink(controller: 'evaluationItem', action: 'createItemModal', id: evaluationSectionId)}"
        preventCloseOutSide="true" class=" btn btn-sm btn-primary"
        label="">
    <i class="ace-icon icon-plus"></i>${message(code: 'evaluationSection.addItem.label')}
</el:modalLink>

</div>