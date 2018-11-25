<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${interview?.interviewStatus}" type="enum"
                     label="${message(code: 'interview.interviewStatus.label', default: 'interviewStatus')}"
                     messagePrefix="EnumInterviewStatus"/>
    <lay:showElement value="${interview?.description}" type="String"
                     label="${message(code: 'interview.description.label', default: 'description')}"/>
    <lay:showElement value="${interview?.recruitmentCycle?.name}" type="string"
                     label="${message(code: 'interview.recruitmentCycle.label', default: 'recruitmentCycle')}"/>
    <lay:showElement value="${interview?.fromDate}" type="ZonedDate"
                     label="${message(code: 'interview.fromDate.label', default: 'fromDate')}"/>
    <lay:showElement value="${interview?.toDate}" type="ZonedDate"
                     label="${message(code: 'interview.toDate.label', default: 'toDate')}"/>
    <lay:showElement value="${interview?.vacancy?.job?.descriptionInfo?.localName}" type="string"
                     label="${message(code: 'interview.vacancy.label', default: 'vacancy')}"/>
    <lay:showElement
            value="${interview?.transientData?.locationName}"
            type="string" label="${message(code: 'interview.locationId.label', default: 'locationId')}"/>
    <lay:showElement value="${interview?.note}" type="String"
                     label="${message(code: 'interview.note.label', default: 'note')}"/>
</lay:showWidget>

<el:row/>

<g:if test="${interview?.committeeRoles}">

    <lay:showWidget size="12" title="${message(code: 'committeeRole.label', default: 'committee role')}">
        <table class="pcpTable table table-bordered table-hover">
            <th class="center pcpHead">${message(code: 'joinedInterviewCommitteeRole.committeeRole.descriptionInfo.localName.label', default: 'committeeRole.label')}</th>
            <th class="center pcpHead">${message(code: 'joinedInterviewCommitteeRole.partyName.label', default: 'partyName')}</th>
            <g:each in="${interview?.committeeRoles?.sort { it.committeeRole.id }}" var="committeeRoles">
                <tr>
                    <td>${committeeRoles?.committeeRole?.descriptionInfo?.localName}</td>
                    <td>${committeeRoles?.partyName}</td>
                </tr>
            </g:each>
        </table>
    </lay:showWidget>
</g:if>
<el:row/>

<g:if test="${interview?.interviewStatus == ps.gov.epsilon.hr.enums.v1.EnumInterviewStatus.OPEN && !isRecruitmentCycleTab}">
<div class="clearfix form-actions text-center">
        <btn:editButton
                onClick="window.location.href='${createLink(controller: 'interview', action: 'edit', params: [encodedId: interview?.encodedId])}'"/>
</div>
</g:if>