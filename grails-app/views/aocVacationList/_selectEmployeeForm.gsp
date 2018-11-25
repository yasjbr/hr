<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumRequestCategory" %>
<msg:warning label="${message(code: 'request.justCommittedEmployee.label')}"/>

%{--request form parent folder should be defined here--}%
<g:hiddenField name="parentFolder" value="vacationRequest"/>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="6" class=" isRequired"
                     controller="vacationType" id="selectedVacationType"
                     action="autocomplete" name="vacationType.id" onchange="handleRequestTypeChange()"
                     label="${message(code: 'vacationRequest.vacationType.label', default: 'vacationType')}"/>
</el:formGroup>

<g:render template="/employee/wrapper" model="[isDisabled            : false,
                                               name                  : 'employeeId',
                                               id                    : 'employeeId',
                                               paramsGenerateFunction: 'employeeParams',
                                               onChange              : 'handleRequestTypeChange()',
                                               size                  : 6]"/>
<el:formGroup>
    <el:select valueMessagePrefix="EnumRequestCategory"
               from="[EnumRequestCategory.ORIGINAL, EnumRequestCategory.CANCEL, EnumRequestCategory.EDIT,
                      EnumRequestCategory.EXTEND, EnumRequestCategory.STOP]" name="requestCategory"
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
        var vacationTypeId= $('#selectedVacationType').val();
        if(employeeId !="" && vacationTypeId!="") {
            var params = {
                requestCategory: requestCategory, employeeId: employeeId, parentFolder: 'aocVacationList',
                'vacationType.id': vacationTypeId
            };

            renderOperationsFormPage(params);
        }
    }

</script>
