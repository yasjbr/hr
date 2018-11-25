
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${recruitmentCycle?.startDate}" type="ZonedDate" label="${message(code:'recruitmentCycle.startDate.label',default:'startDate')}" />
    <lay:showElement value="${recruitmentCycle?.endDate}" type="ZonedDate" label="${message(code:'recruitmentCycle.endDate.label',default:'endDate')}" />
    <lay:showElement value="${recruitmentCycle?.description}" type="String" label="${message(code:'recruitmentCycle.description.label',default:'description')}" />
</lay:showWidget>


<el:row />

<div class="clearfix form-actions text-center">
<g:if test="${recruitmentCycle?.currentRecruitmentCyclePhase?.requisitionAnnouncementStatus in [ps.gov.epsilon.hr.enums.v1.EnumRequisitionAnnouncementStatus.NEW, ps.gov.epsilon.hr.enums.v1.EnumRequisitionAnnouncementStatus.OPEN]}">
    <g:set var='myArray' value='[encodedId: "${recruitmentCycle?.encodedId}"]' />
    <btn:editButton onClick="window.location.href='${createLink(controller: 'recruitmentCycle', action: 'edit', params: myArray)}'"/>
</g:if>

<g:if test="${recruitmentCycle?.currentRecruitmentCyclePhase?.requisitionAnnouncementStatus != ps.gov.epsilon.hr.enums.v1.EnumRequisitionAnnouncementStatus.CLOSED}">
    <g:set var='myArray' value='[encodedId: "${recruitmentCycle?.encodedId}"]' />
    <btn:button message="${message(code:'recruitmentCycle.nextPhase.label')}" onClick="window.location.href='${createLink(controller: 'recruitmentCycle', action: 'changePhase', params: myArray)}'"/>
</g:if>
    %{--<btn:listButton onClick="window.location.href='${createLink(controller: 'recruitmentCycle', action: 'list')}'"/>--}%

<g:if test="${recruitmentCycle?.currentRecruitmentCyclePhase?.requisitionAnnouncementStatus == ps.gov.epsilon.hr.enums.v1.EnumRequisitionAnnouncementStatus.REVIEWED}">
    <el:form action="#" style="display: none;" name="jobRequisitionReportSearchForm">
        <el:hiddenField name="recruitmentCycle.id" value="${recruitmentCycle?.id}" />
        <el:hiddenField name="isSoldier" value="false" />
    </el:form>
    <report:showStatic iconWithText="true" searchFromName="jobRequisitionReportSearchForm"
                       title="${message(code:'jobRequisition.entities')}"
                       reportName="jobRequisitionReport"
                       domain="jobRequisition"
                       method="getJobRequisitionReportData"
                       format="pdf" reportFormPrefix="JobRequisition" delimiter="${message(code: 'jobRequisition.civil.label')}" />



    <el:form action="#" style="display: none;" name="jobRequisitionSoldierReportSearchForm">
        <el:hiddenField name="recruitmentCycle.id" value="${recruitmentCycle?.id}" />
        <el:hiddenField name="isSoldier" value="true" />
    </el:form>
    <report:showStatic iconWithText="true" searchFromName="jobRequisitionSoldierReportSearchForm"
                       title="${message(code:'jobRequisition.entities')}"
                       reportName="jobRequisitionSoldierReport"
                       domain="jobRequisition"
                       method="getJobRequisitionReportData"
                       format="pdf" reportFormPrefix="SoldierJobRequisition"  delimiter="${message(code: 'jobRequisition.soldier.label')}" />


</g:if>
</div>

