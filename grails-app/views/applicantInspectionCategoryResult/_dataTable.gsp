<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumApplicantStatus" %>
<lay:collapseWidget id="applicantInspectionCategoryResultCollapseWidget" icon="icon-search"
                    title="${message(code: 'applicantInspectionCategoryResult.search.label')}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton class="btn btn-sm btn-info2"
                              accessUrl="${createLink(controller: tabEntityName, action: 'create', params: ["applicantId": params.holderEntityId])}"
                              onclick="renderInLineCreate()"
                              label="${tabTitle}">
                <i class="icon-plus"></i>
            </btn:createButton>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="applicantInspectionCategoryResultSearchForm">
            <el:hiddenField name="applicant.id" value="${params.holderEntityId}"/>
            <g:render template="/applicantInspectionCategoryResult/search" model="[:]"/>
            <el:formButton functionName="search"
                           onClick="_dataTables['applicantInspectionCategoryResultTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('applicantInspectionCategoryResultSearchForm');_dataTables['applicantInspectionCategoryResultTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>

<el:dataTable id="applicantInspectionCategoryResultTable" searchFormName="applicantInspectionCategoryResultSearchForm"

              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="applicantInspectionCategoryResult"
              spaceBefore="true" hasRow="true" action="filter"
              serviceName="applicantInspectionCategoryResult"
              domainColumns="${domainColumns}">

    <g:if test="${isInLineActions}">

        <el:dataTableAction accessUrl="${createLink(controller: 'applicantInspectionCategoryResult', action: 'show')}"
                            functionName="renderInLineShow"
                            actionParams="encodedId"
                            type="function" class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [tabEntity], default: 'show applicantInspectionCategoryResult')}"/>

        <el:dataTableAction accessUrl="${createLink(controller: 'applicantInspectionCategoryResult', action: 'edit')}"
                            functionName="renderInLineEdit"
                            actionParams="encodedId"
                            type="function"
                            showFunction="canEditInspection"
                            class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [tabEntity], default: 'edit applicantInspectionCategoryResult')}"/>

        <el:dataTableAction controller="applicantInspectionCategoryResult"
                            actionParams="encodedId"
                            action="delete"
                            showFunction="canDeleteInspection"
                            class="red icon-trash"
                            type="confirm-delete"
                            message="${message(code: 'default.delete.label', args: [tabEntity], default: 'delete applicantInspectionCategoryResult')}"/>
    </g:if>
    <g:else>
        <el:dataTableAction controller="applicantInspectionCategoryResult" actionParams="encodedId" action="show"
                            class="green icon-eye"
                            message="${message(code: 'default.show.label', args: [entity], default: 'show applicantInspectionCategoryResult')}"/>
        <el:dataTableAction controller="applicantInspectionCategoryResult" actionParams="encodedId" action="edit"
                            class="blue icon-pencil"
                            message="${message(code: 'default.edit.label', args: [entity], default: 'edit applicantInspectionCategoryResult')}"/>
        <el:dataTableAction controller="applicantInspectionCategoryResult" actionParams="encodedId" action="delete"
                            class="red icon-trash"
                            type="confirm-delete"
                            message="${message(code: 'default.delete.label', args: [entity], default: 'delete applicantInspectionCategoryResult')}"/>
    </g:else>

</el:dataTable>


<script>

    gui.initAll.init($('#applicantInspectionCategoryResultCollapseWidget'));


    function canDeleteInspection(row) {
        if (row.applicantStatus == "${ EnumApplicantStatus.NEW.toString()}" ||
                row.applicantStatus == "${EnumApplicantStatus.ADD_TO_LIST.toString()}" ||
                row.applicantStatus == "${EnumApplicantStatus.INTERVIEW_ABSENCE.toString()}" ||
                row.applicantStatus == "${EnumApplicantStatus.UNDER_INTERVIEW.toString()}") {
            return true
        }
        return false
    }

    function canEditInspection(row) {
        if (row.applicantStatus == "${ EnumApplicantStatus.NEW.toString()}" ||
                row.applicantStatus == "${EnumApplicantStatus.ADD_TO_LIST.toString()}" ||
                row.applicantStatus == "${EnumApplicantStatus.INTERVIEW_ABSENCE.toString()}" ||
                row.applicantStatus == "${EnumApplicantStatus.UNDER_INTERVIEW.toString()}") {
            return true
        }
    }

</script>