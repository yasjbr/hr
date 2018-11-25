<el:dataTable id="personLanguageInfoTable"
              searchFormName="personLanguageInfoSearchForm"

              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12"
              controller="personLanguageInfo"
              spaceBefore="true" hasRow="true"
              action="filter"
              serviceName="personLanguageInfo"
              domainColumns="${domainColumns}">

    <g:if test="${isInLineActions}">
        <el:dataTableAction accessUrl="${createLink(controller: 'personLanguageInfo',action: 'show')}" functionName="renderInLineShow" type="function" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show personLanguageInfo')}"/>
        <el:dataTableAction accessUrl="${createLink(controller: 'personLanguageInfo',action: 'edit')}" functionName="renderInLineEdit" type="function" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity], default: 'edit personLanguageInfo')}"/>

    </g:if>
    <g:else>
        <el:dataTableAction controller="personLanguageInfo" action="show" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show personLanguageInfo')}"/>
        <el:dataTableAction controller="personLanguageInfo" action="edit" class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity],default: 'edit personLanguageInfo')}"/>
    </g:else>
    <el:dataTableAction controller="personLanguageInfo" action="delete" class="red icon-trash" type="confirm-delete"
                        message="${message(code: 'default.delete.label', args: [entity], default: 'delete personLanguageInfo')}"/>
</el:dataTable>