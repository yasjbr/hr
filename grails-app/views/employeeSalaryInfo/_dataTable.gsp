<el:dataTable id="employeeSalaryInfoTable"
              searchFormName="employeeSalaryInfoSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="employeeSalaryInfo"
              spaceBefore="true" hasRow="true"
              action="filter"
              serviceName="employeeSalaryInfo"
              domainColumns="${domainColumns}">
    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'personLiveStatus', action: 'show')}"
                            actionParams="encodedId" functionName="renderInLineShow" type="function"
                            class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity],
                                    default: 'show employeeSalaryInfo')}"/>
    </g:if>
    <g:else>
        <el:dataTableAction controller="employeeSalaryInfo" action="show"
                            actionParams="encodedId" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity],
                                    default: 'show employeeSalaryInfo')}"/>
    </g:else>
</el:dataTable>

