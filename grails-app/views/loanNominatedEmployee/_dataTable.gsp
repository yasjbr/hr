<el:dataTable id="loanNominatedEmployeeTable"
              searchFormName="loanNominatedEmployeeSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="loanNominatedEmployee"
              spaceBefore="true"
              hasRow="true"
              action="filter"
              serviceName="loanNominatedEmployee"
              domainColumns="${domainColumns}">


    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'loanNominatedEmployee', action: 'show')}"
                             functionName="renderInLineShow" type="function" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity],
                                    default: 'show loanNominatedEmployee')}"/>

    </g:if>

    <g:else>
        <el:dataTableAction controller="loanNominatedEmployee" action="show"
                            actionParams="encodedId" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity],
                                    default: 'show loanNominatedEmployee')}"/>

    </g:else>

    <el:dataTableAction controller="loanNominatedEmployee" action="delete"
                        showFunction="viewEditAction" actionParams="encodedId" class="red icon-trash" type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity],
                                default: 'delete loanNominatedEmployee')}"/>


</el:dataTable>

