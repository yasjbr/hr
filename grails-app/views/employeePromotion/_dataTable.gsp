<el:dataTable id="employeePromotionTable"
              searchFormName="employeePromotionSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="employeePromotion"
              spaceBefore="true" hasRow="true"
              action="filter"
              serviceName="employeePromotion"
              domainColumns="${domainColumns}">

    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'employeePromotion',action: 'show')}"
                            actionParams="encodedId" functionName="renderInLineShow" type="function" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity],
                                    default: 'show employeePromotion')}"/>
    </g:if>

    <g:else>
        <el:dataTableAction controller="employeePromotion" action="show"
                            actionParams="encodedId" class="green icon-eye"
                            message="${message(code:'default.show.label',args:[entity],
                                    default:'show employeePromotion')}" />

        <el:dataTableAction controller="employeePromotion" action="edit"
                            actionParams="encodedId" class="blue icon-pencil"
                            message="${message(code:'default.edit.label',
                                    args:[entity],default:'edit employeePromotion')}" />

        <el:dataTableAction controller="employeePromotion" action="delete"
                            actionParams="encodedId" class="red icon-trash" type="confirm-delete"
                            message="${message(code:'default.delete.label',args:[entity],
                                    default:'delete employeePromotion')}" />
    </g:else>
</el:dataTable>