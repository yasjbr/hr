<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestCategory" %>
<msg:warning label="${message(code: 'request.justCommittedEmployee.label')}"/>

%{--request form parent folder should be defined here--}%
<g:hiddenField name="parentFolder" value="childRequest"/>

<g:render template="/employee/wrapper" model="[isDisabled            : false,
                                               name                  : 'employeeId',
                                               id                    : 'employeeId',
                                               paramsGenerateFunction: 'employeeParams',
                                               size                  : 6]"/>

<el:formGroup>
    <el:select valueMessagePrefix="EnumRequestCategory"
               from="[EnumRequestCategory.ORIGINAL, EnumRequestCategory.CANCEL, EnumRequestCategory.EDIT]" name="requestCategory"
               size="6" class=" isRequired" onchange="handleRequestTypeChange()"
               label="${message(code: 'aocCorrespondenceList.requestType.label', default: 'requestType')}"
               value="${EnumRequestCategory.ORIGINAL}"/>
</el:formGroup>

<script>

    /**
     * to get only employee with status COMMITTED
     * TODO categoryStatusId is ignored currently, it should handle centralized with AOC state
     */
    function employeeParams() {
        var searchParams = {};
        searchParams['firm.id']= $('#firmId').val();
        if(searchParams['firm.id'] == ""){
            searchParams['firm.id']="-1";
        }
        searchParams.noFirmCategoryStatusId = "${ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory.COMMITTED.name()}";
        return searchParams;
    }

    function handleRequestTypeChange() {
        var requestCategory= $('#requestCategory').val();
        var employeeId= $('#employeeId').val();
        if(employeeId !="") {
            var params = {
                requestCategory: requestCategory, employeeId: employeeId, parentFolder: 'aocChildList'
            };
            renderOperationsFormPage(params);
        }
    }

</script>
