<msg:warning label="${message(code:'recallToService.unCommittedEmployee.label')}" />


%{--request form parent folder should be defined here--}%
<g:hiddenField name="parentFolder" value="employmentServiceRequest"/>
<g:hiddenField name="formName" value="formReturnToService"/>

<g:render template="/employee/wrapper" model="[isDisabled            : false,
                                               name                  : 'employeeId',
                                               id                    : 'employeeId',
                                               paramsGenerateFunction: 'employeeParams',
                                               size                  : 6]"/>
<el:formGroup>
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="6"
                     class=" isRequired"
                     paramsGenerateFunction="reasonParams"
                     controller="serviceActionReason"
                     action="autocomplete"
                     id="serviceActionReasonId"
                     name="serviceActionReasonId"
                     label="${message(code: 'recallToService.EnumServiceActionReason.label', default: 'serviceActionReason')}"
                     values="${[[employmentServiceRequest?.serviceActionReason?.id, employmentServiceRequest?.serviceActionReason?.descriptionInfo?.localName]]}"/>


%{--<el:select--}%
            %{--valueMessagePrefix="EnumServiceActionReason"--}%
            %{--from="${ps.gov.epsilon.hr.enums.employmentService.v1.EnumServiceActionReason.values().key}"--}%
            %{--name="serviceActionReasonName"--}%
            %{--size="6"--}%
            %{--class=" isRequired"--}%
            %{--label="${message(code: 'recallToService.EnumServiceActionReason.label', default: 'serviceActionReason')}"/>--}%
</el:formGroup>

<script>
    function employeeParams() {
        return {'firmId': $('#firmId').val(),'noFirmCategoryStatusId':'${ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory.UNCOMMITTED.name()}', 'allowReturnToService':true}
    }
    function reasonParams() {
        return {
            "firm.id": $('#firmId').val(),
            "isRelatedToEndOfService_string": "NO"
        };
    }
</script>
