<el:formGroup>
    <el:textField name="name" size="8" class="" label="${message(code: 'profileNotice.name.label', default: 'name')}"
                  value="${profileNotice?.name}"/>
</el:formGroup>
<el:formGroup>
    <el:dateField name="noticeDate" size="8" class="" value="${profileNotice?.noticeDate}"
                  label="${message(code: 'profileNotice.noticeDate.label', default: 'noticeDate')}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="profileNoticeCategory"
                     action="autocomplete" name="profileNoticeCategory.id"
                     label="${message(code: 'profileNotice.profileNoticeCategory.label', default: 'profileNoticeCategory')}"
                     values="${[[profileNotice?.profileNoticeCategory?.id, profileNotice?.profileNoticeCategory?.descriptionInfo?.localName]]}"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="profileNoticeReason" size="8" class=""
                  label="${message(code: 'profileNotice.profileNoticeReason.label', default: 'profileNoticeReason')}"
                  value="${profileNotice?.profileNoticeReason}"/>
</el:formGroup>
<el:formGroup>

    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="8"
                     class=" isRequired"
                     controller="pcore"
                     action="organizationAutoComplete"
                     name="sourceOrganizationId"
                     label="${message(code: 'profileNotice.sourceOrganizationId.label', default: 'sourceOrganizationId')}"
                     values="${[[profileNotice?.sourceOrganizationId, profileNotice?.transientData?.sourceOrganizationName]]}"
                     id="organizationAutoComplete"/>


    %{--<el:integerField name="sourceOrganizationId" size="8" class=" isNumber"--}%
                     %{--label="${message(code: 'profileNotice.sourceOrganizationId.label', default: 'sourceOrganizationId')}"--}%
                     %{--value="${profileNotice?.sourceOrganizationId}"/>--}%
</el:formGroup>
<el:formGroup>
    <el:textField name="presentedBy" size="8" class=""
                  label="${message(code: 'profileNotice.presentedBy.label', default: 'presentedBy')}"
                  value="${profileNotice?.presentedBy}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="firm"
                     action="autocomplete" name="employeeFirmId"
                     label="${message(code: 'profileNotice.firm.label', default: 'firm')}"
                     values="${[[profileNotice?.employee?.firm?.id, profileNotice?.employee?.firm?.name]]}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="employee"
                     action="autocomplete" name="employee.id" paramsGenerateFunction="employeeParams"
                     label="${message(code: 'profileNotice.employee.label', default: 'employee')}"
                     values="${[[profileNotice?.employee?.id, profileNotice?.employee?.toString()]]}"/>
</el:formGroup>
<el:formGroup>
    <el:textAreaDescription name="noticeText" size="8" class=" isRequired"
                  label="${message(code: 'profileNotice.noticeText.label', default: 'noticeText')}"
                  value="${profileNotice?.noticeText}"/>
</el:formGroup>
%{--<el:formGroup>--}%
    %{--<el:select valueMessagePrefix="EnumProfileNoticeStatus"--}%
               %{--from="${ps.gov.epsilon.hr.enums.profile.v1.EnumProfileNoticeStatus.values()}" name="profileNoticeStatus"--}%
               %{--size="8" class=" isRequired"--}%
               %{--label="${message(code: 'profileNotice.profileNoticeStatus.label', default: 'profileNoticeStatus')}"--}%
               %{--value="${profileNotice?.profileNoticeStatus}"/>--}%
%{--</el:formGroup>--}%
<g:hiddenField name="profileNoticeStatus" value="${profileNotice?.profileNoticeStatus}" />

<script>
    /**
     * to filter employees by selected firm
     */
    function employeeParams() {
        var searchParams = {};
        searchParams['firm.id']= $('#employeeFirmId').val();
        return searchParams;
    }
</script>