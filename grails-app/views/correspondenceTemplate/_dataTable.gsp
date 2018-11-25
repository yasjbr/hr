<el:dataTable id="correspondenceTemplateTable" searchFormName="correspondenceTemplateSearchForm"
              dataTableTitle="${title}" isSingleSelect="${singleSelect?:false}"
              hasCheckbox="true" widthClass="col-sm-12" controller="correspondenceTemplate"
              spaceBefore="true" hasRow="true" action="filter" serviceName="correspondenceTemplate">


    <g:if test="${!disableTools}">
        <el:dataTableAction controller="correspondenceTemplate" action="show" actionParams="encodedId"
                            class="green icon-eye" message="${message(code:'default.show.label',
                args:[entity],default:'show correspondenceTemplate')}" />

        <el:dataTableAction controller="correspondenceTemplate" action="edit" actionParams="encodedId"
                            class="blue icon-pencil" message="${message(code:'default.edit.label',
                args:[entity],default:'edit correspondenceTemplate')}" />

        <el:dataTableAction controller="correspondenceTemplate" action="delete" actionParams="encodedId"
                            class="red icon-trash" type="confirm-delete" showFunction="manageExecuteDelete" message="${message(code:'default.delete.label',
                args:[entity],default:'delete correspondenceTemplate')}" />
    </g:if>

</el:dataTable>