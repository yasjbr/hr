<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestType"%>
<msg:warning label="${message(code:'request.justCommittedEmployee.label')}" />

%{--request form parent folder should be defined here--}%
<g:hiddenField name="parentFolder" value="promotionRequest"/>

<g:render template="/employee/wrapper" model="[isDisabled            : false,
                                               name                  : 'employeeId',
                                               id                    : 'employeeId',
                                               paramsGenerateFunction: 'employeeParams',
                                               size                  : 6]"/>

<el:formGroup>
    <el:select valueMessagePrefix="EnumRequestType"
               from="${[EnumRequestType.PERIOD_SETTLEMENT, EnumRequestType.PERIOD_SETTLEMENT_CURRENT_ARREST,
                        EnumRequestType.PERIOD_SETTLEMENT_EMPLOYMENT_PERIOD, EnumRequestType.PERIOD_SETTLEMENT_OLD_ARREST,
                        EnumRequestType.SITUATION_SETTLEMENT, EnumRequestType.EXCEPTIONAL_REQUEST]}"
               name="requestType" size="6" class=" isRequired"
               label="${message(code: 'promotionRequest.requestType.label', default: 'requestType')}"/>

</el:formGroup>

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
