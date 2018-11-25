<msg:warning label="${message(code: 'request.justCommittedEmployee.label')}"/>

%{--request form parent folder should be defined here--}%
<g:hiddenField name="parentFolder" value="suspensionRequest"/>

<el:formGroup>
    <el:select valueMessagePrefix="EnumSuspensionType"
               from="${ps.gov.epsilon.hr.enums.suspension.v1.EnumSuspensionType.values()}"
               name="suspensionType"
               size="6" class=" isRequired"
               label="${message(code: 'suspensionRequest.suspensionType.label', default: 'suspensionType')}"/>

</el:formGroup>


<g:render template="/employee/wrapper" model="[isDisabled            : false,
                                               paramsGenerateFunction: 'employeeParams',
                                               size                  : 6]"/>

<script>

    /**
     * to get only employee with status COMMITTED
     * TODO categoryStatusId is ignored currently, it should handle centralized with AOC state
     */
    function employeeParams() {
        var searchParams = {};
        searchParams['firm.id']= $('#firmId').val();
        searchParams.noFirmCategoryStatusId = "${ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory.COMMITTED.name()}";
        return searchParams;
    }

</script>
