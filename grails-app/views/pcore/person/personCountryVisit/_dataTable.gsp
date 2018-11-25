

<el:dataTable id="personCountryVisitTable" searchFormName="personCountryVisitSearchForm"

              dataTableTitle="${title}"
              hasCheckbox="true"
              widthClass="col-sm-12"
              controller="personCountryVisit"
              spaceBefore="true" hasRow="true"
              action="filter"
              serviceName="personCountryVisit"
              domainColumns="${domainColumns}">


    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'personCountryVisit',action: 'show')}" functionName="renderInLineShow" type="function" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show personCountryVisit')}"/>
        <el:dataTableAction accessUrl="${createLink(controller: 'personCountryVisit',action: 'edit')}" functionName="renderInLineEdit" type="function" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity], default: 'edit personCountryVisit')}"/>

    </g:if>
    <g:else>
        <el:dataTableAction controller="personCountryVisit" action="show" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show personCountryVisit')}"/>
        <el:dataTableAction controller="personCountryVisit" action="edit" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity],default: 'edit personCountryVisit')}"/>
    </g:else>
    <el:dataTableAction controller="personCountryVisit" action="delete" class="red icon-trash" type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete personCountryVisit')}"/>
</el:dataTable>
