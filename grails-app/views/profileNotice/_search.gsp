<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" " controller="firm"
                     action="autocomplete" name="employeeFirmId"
                     label="${message(code: 'profileNotice.firm.label', default: 'firm')}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" " controller="employee"
                     action="autocomplete" name="employee.id" paramsGenerateFunction="employeeParams"
                     label="${message(code: 'profileNotice.employee.label', default: 'employee')}"/>
</el:formGroup>

<el:formGroup>

    <el:textField name="name" size="8" class="" label="${message(code: 'profileNotice.name.label', default: 'name')}"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="noticeText" size="8" class=""
                  label="${message(code: 'profileNotice.noticeText.label', default: 'noticeText')}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="profileNoticeCategory"
                     action="autocomplete" name="profileNoticeCategory.id"
                     label="${message(code: 'profileNotice.profileNoticeCategory.label', default: 'profileNoticeCategory')}"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="profileNoticeReason" size="8" class=""
                  label="${message(code: 'profileNotice.profileNoticeReason.label', default: 'profileNoticeReason')}"/>
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumProfileNoticeStatus"
               from="${ps.gov.epsilon.hr.enums.profile.v1.EnumProfileNoticeStatus.values()}" name="profileNoticeStatus"
               size="8" class=""
               label="${message(code: 'profileNotice.profileNoticeStatus.label', default: 'profileNoticeStatus')}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="8"
                     class=" "
                     controller="pcore"
                     action="organizationAutoComplete"
                     name="sourceOrganizationId"
                     label="${message(code: 'profileNotice.sourceOrganizationId.label', default: 'sourceOrganizationId')}"
                     id="organizationAutoComplete"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="presentedBy" size="8" class=""
                  label="${message(code: 'profileNotice.presentedBy.label', default: 'presentedBy')}"/>
</el:formGroup>

<script>
    /**
     * to filter employees by selected firm
     */
    function employeeParams() {
        var searchParams = {};
        searchParams.firmId = $('#employeeFirmId').val();
        if(searchParams.firmId==""){
            searchParams.firmId=-1;
        }
        return searchParams;
    }
</script>