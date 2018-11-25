<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'joinedRecruitmentCycleDepartment.entities', default: 'JoinedRecruitmentCycleDepartment List')}" />
    <g:set var="entity" value="${message(code: 'joinedRecruitmentCycleDepartment.entity', default: 'JoinedRecruitmentCycleDepartment')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'JoinedRecruitmentCycleDepartment List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="joinedRecruitmentCycleDepartmentCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'recruitmentCycle',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="joinedRecruitmentCycleDepartmentSearchForm">
            <el:hiddenField name="encodedRecruitmentCycleId" value="${recruitmentCycle?.encodedId}" />
            <g:render template="/joinedRecruitmentCycleDepartment/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['joinedRecruitmentCycleDepartmentTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('joinedRecruitmentCycleDepartmentSearchForm');_dataTables['joinedRecruitmentCycleDepartmentTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:row/>
<el:row/>
<el:row>

    <lay:showWidget size="6" title="">
        <lay:showElement value="${recruitmentCycle?.name}" type="String" label="${message(code:'recruitmentCycle.name.label',default:'transactionNumber')}" />
        <lay:showElement value="${recruitmentCycle?.currentRecruitmentCyclePhase?.requisitionAnnouncementStatus}" type="enum" label="${message(code:'recruitmentCycle.currentRecruitmentCyclePhase.label',default:'requisitionAnnouncementStatus')}" messagePrefix="EnumRequisitionAnnouncementStatus" />
    </lay:showWidget>

    <lay:showWidget size="6" title="">
        <lay:showElement value="${recruitmentCycle?.startDate}" type="ZonedDate" label="${message(code:'recruitmentCycle.startDate.label',default:'startDate')}" />
        <lay:showElement value="${recruitmentCycle?.endDate}" type="ZonedDate" label="${message(code:'recruitmentCycle.endDate.label',default:'endDate')}" />
    </lay:showWidget>
</el:row>

<el:dataTable id="joinedRecruitmentCycleDepartmentTable" searchFormName="joinedRecruitmentCycleDepartmentSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="joinedRecruitmentCycleDepartment" spaceBefore="true" hasRow="true" action="filter" serviceName="joinedRecruitmentCycleDepartment">
    <el:dataTableAction controller="joinedRecruitmentCycleDepartment" action="show" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show joinedRecruitmentCycleDepartment')}" />
    %{--<el:dataTableAction controller="joinedRecruitmentCycleDepartment" action="edit" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit joinedRecruitmentCycleDepartment')}" />--}%
    %{--<el:dataTableAction controller="joinedRecruitmentCycleDepartment" action="delete" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete joinedRecruitmentCycleDepartment')}" />--}%
</el:dataTable>
</body>
</html>