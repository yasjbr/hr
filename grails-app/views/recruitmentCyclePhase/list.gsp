<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'recruitmentCyclePhase.entities', default: 'RecruitmentCyclePhase List')}" />
    <g:set var="entity" value="${message(code: 'recruitmentCyclePhase.entity', default: 'RecruitmentCyclePhase')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'RecruitmentCyclePhase List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="recruitmentCyclePhaseCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'recruitmentCycle',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="recruitmentCyclePhaseSearchForm">
            <el:hiddenField name="encodedRecruitmentCycleId" value="${recruitmentCycle?.encodedId}" />
            <g:render template="/recruitmentCyclePhase/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['recruitmentCyclePhaseTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('recruitmentCyclePhaseSearchForm');_dataTables['recruitmentCyclePhaseTable'].draw();"/>
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


<el:dataTable id="recruitmentCyclePhaseTable" searchFormName="recruitmentCyclePhaseSearchForm"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="recruitmentCyclePhase" spaceBefore="true" hasRow="true" action="filter" serviceName="recruitmentCyclePhase">
    <el:dataTableAction controller="recruitmentCyclePhase" action="show" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show recruitmentCyclePhase')}" />
    %{--<el:dataTableAction controller="recruitmentCyclePhase" action="edit" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit recruitmentCyclePhase')}" />--}%
    %{--<el:dataTableAction controller="recruitmentCyclePhase" action="delete" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete recruitmentCyclePhase')}" />--}%
</el:dataTable>
</body>
</html>