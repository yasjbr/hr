%{--<msg:warning label="${message(code: 'request.justCommittedEmployee.label')}"/>--}%

%{--request form parent folder should be defined here--}%
<g:hiddenField name="parentFolder" value="returnFromAbsenceRequest"/>


<g:render template="/employee/wrapper" model="[isDisabled            : false,
                                               name                  : 'employeeId',
                                               id                    : 'employeeId',
                                               paramsGenerateFunction: 'employeeParams',
                                               size                  : 6]"/>

<el:formGroup>
    <el:autocomplete optionKey="id" paramsGenerateFunction="absenceParam" optionValue="info" size="6"
                     class=" isRequired" controller="absence" action="autocomplete"
                     name="absenceId"
                     id="absenceId"
                     label="${message(code:'absence.label',default:'absence')}"
                     values="${[[returnFromAbsenceRequest?.absence?.id,returnFromAbsenceRequest?.absence?.descriptionInfo?.localName]]}" />
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

    function absenceParam() {
        var searchParams = {};
        searchParams.firmId = $('#firmId').val();
        searchParams.employeeId = $("#employeeId").val();
        searchParams.excludedStatusList = 'RETURNED,CLOSED';
        return searchParams;
    }

    $("#employeeId").on("select2:close", function (e) {
        var value = $('#employeeId').val();
        if(value){
            $('#absenceId').val("");
            $('#absenceId').trigger('change');
        }
    });

    $("#absenceId").on("select2:close", function (e) {
        var value = $('#absenceId').val();
        var employeeIdValue = $('#employeeId').val();
        if(value && !employeeIdValue){
            $.ajax({
                url: '${createLink(controller: 'absence',action: 'getInstance')}',
                type: 'POST',
                data: {
                    id: value
                },
                dataType: 'json',
                beforeSend: function(jqXHR,settings) {
                    guiLoading.show();
                },
                error: function(jqXHR) {
                    guiLoading.hide();
                },
                success: function(json) {
                    guiLoading.hide();
                    if(json.employeeId){
                        $("#employeeId").val(json.employeeId);
                        var newOption = new Option(json.employeeName,json.employeeId, true, true);
                        $('#employeeId').append(newOption);
                        $('#employeeId').trigger('change');
                    }
                }
            });
        }
    });


</script>
