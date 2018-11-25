<%@ page import="ps.gov.epsilon.hr.enums.profile.v1.EnumProfileStatus; ps.gov.epsilon.hr.enums.profile.v1.EnumProfileNoticeStatus" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'profileNotice.entity', default: 'ProfileNotice List')}"/>
    <g:set var="title" value="${message(code: 'default.show.label', args: [entity], default: 'ProfileNotice List')}"/>
    <title>${title}</title>
</head>

<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller: 'profileNotice', action: 'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${profileNotice?.employee}" type="Employee"
                     label="${message(code: 'profileNotice.employee.label', default: 'employee')}"/>
    <lay:showElement value="${profileNotice?.employee?.profileStatus ?: EnumProfileStatus.ACTIVE}" type="enum"
                     label="${message(code: 'employee.profileStatus.label', default: 'Employee profile status')}"
                     messagePrefix="EnumProfileStatus"/>
    <lay:showElement value="${profileNotice?.name}" type="String"
                     label="${message(code: 'profileNotice.name.label', default: 'name')}"/>
    <lay:showElement value="${profileNotice?.noticeText}" type="String"
                     label="${message(code: 'profileNotice.noticeText.label', default: 'noticeText')}"/>
    <lay:showElement value="${profileNotice?.transientData.sourceOrganizationName}" type="Long"
                     label="${message(code: 'profileNotice.sourceOrganizationId.label', default: 'sourceOrganizationId')}"/>
    <lay:showElement value="${profileNotice?.presentedBy}" type="String"
                     label="${message(code: 'profileNotice.presentedBy.label', default: 'presentedBy')}"/>
    <lay:showElement value="${profileNotice?.profileNoticeCategory?.descriptionInfo?.localName}"
                     type="ProfileNoticeCategory"
                     label="${message(code: 'profileNotice.profileNoticeCategory.label', default: 'profileNoticeCategory')}"/>
%{--<lay:showElement value="${profileNotice?.profileNoticeNotes}" type="Set" label="${message(code:'profileNotice.profileNoticeNotes.label',default:'profileNoticeNotes')}" />--}%
    <lay:showElement value="${profileNotice?.profileNoticeReason}" type="String"
                     label="${message(code: 'profileNotice.profileNoticeReason.label', default: 'profileNoticeReason')}"/>
    <lay:showElement value="${profileNotice?.profileNoticeStatus}" type="enum"
                     label="${message(code: 'profileNotice.profileNoticeStatus.label', default: 'profileNoticeStatus')}"
                     messagePrefix="EnumProfileNoticeStatus"/>

</lay:showWidget>


<g:if test="${!profileNotice?.profileNoticeNotes?.isEmpty()}">
    <el:row/>
    <el:row/>

    <div class="col-md-12">
        <lay:table styleNumber="1" id="notesTable1" title="${message(code: 'profileNotice.profileNoticeNotes.label')}">
            <lay:tableHead title="${message(code: 'profileNoticeNote.note.label')}"/>
            <lay:tableHead title="${message(code: 'profileNoticeNote.noteDate.label')}"/>
            <lay:tableHead title="${message(code: 'default.createdBy.label')}"/>
            <g:each in="${profileNotice?.profileNoticeNotes}" var="note">
                <rowElement>
                    <tr class='center' id='row-0'>
                        <td class='center'>${note.note}</td>
                        <td class='center'>${note.noteDate.format(ps.police.common.utils.v1.PCPUtils.ZONED_DATE_FORMATTER)}</td>
                        <td class='center'>${note.trackingInfo?.createdBy}</td>
                        %{--<td class='center'></td>--}%
                    </tr>

                </rowElement>
            </g:each>
        </lay:table>
    </div>
</g:if>
<el:row/>
<div class="clearfix form-actions text-center">

    <g:if test="${!profileNotice?.employee?.profileStatus || profileNotice?.employee?.profileStatus == ps.gov.epsilon.hr.enums.profile.v1.EnumProfileStatus.ACTIVE}">
        <el:modalLink
                link="${createLink(controller: 'employee', action: 'changeEmployeeProfileStatusModal', params: [encodedId: profileNotice?.employee?.encodedId,
                                                                                                                'firm.id': profileNotice?.employee?.firm?.id,
                                                                                                                newProfileStatus:EnumProfileStatus.LOCKED])}"
                preventCloseOutSide="true" class=" btn btn-sm btn-danger"
                label="">
            <i class="ace-icon icon-lock"></i>${message(code: 'employee.lockProfile.label', default: 'Lock profile')}
        </el:modalLink>
    </g:if>

    <g:if test="${profileNotice?.profileNoticeStatus in [EnumProfileNoticeStatus.NEW, EnumProfileNoticeStatus.ACTIVE]}">
        <el:modalLink
                link="${createLink(controller: 'profileNotice', action: 'endNoticeModal', params: [encodedId: profileNotice?.encodedId])}"
                preventCloseOutSide="true" class=" btn btn-sm btn-success"
                label="">
            <i class="ace-icon icon-cancel-circled"></i>${message(code: 'profileNotice.endNotice.label', default: 'End Notice')}
        </el:modalLink>
    </g:if>

</div>

</body>
</html>