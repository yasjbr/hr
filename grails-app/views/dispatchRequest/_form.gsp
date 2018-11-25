<el:hiddenField name="employee.id" value="${dispatchRequest?.employee?.id}"/>
<g:if test="${!hideEmployeeInfo}">
<g:render template="/employee/wrapperForm" model="[employee: dispatchRequest?.employee]"/>
</g:if>
<lay:widget transparent="true" color="blue" icon="icon-info-4" title="${g.message(code: "request.info.label")}">
    <lay:widgetBody>
        <br/>
        <el:formGroup>
            <el:select valueMessagePrefix="EnumDispatchType"
                       from="${ps.gov.epsilon.hr.enums.dispatch.v1.EnumDispatchType.values()}"
                       name="dispatchType"
                       id="dispatchType"
                       size="6"
                       class=" isRequired"
                       label="${message(code: 'dispatchRequest.dispatchType.label', default: 'dispatchType')}"
                       value="${dispatchRequest?.dispatchType}"/>

            <el:dateField name="requestDate" size="6" class=" isRequired" isMaxDate="true"
                          label="${message(code: 'dispatchRequest.requestDate.label', default: 'requestDate')}"
                          value="${dispatchRequest?.requestDate}"/>
        </el:formGroup>
        <el:formGroup>
            <el:dateField id="fromDate" name="fromDate" size="6" class=" isRequired" setMinDateFor="toDate"
                          label="${message(code: 'dispatchRequest.fromDate.label', default: 'fromDate')}"
                          value="${dispatchRequest?.fromDate}"/>
            <el:dateField id="toDate" name="toDate" size="6" class=" isRequired"
                          label="${message(code: 'dispatchRequest.toDate.label', default: 'toDate')}"
                          value="${dispatchRequest?.toDate}"/>
        </el:formGroup>
        <el:formGroup>
            <el:dateField id="nextVerificationDate" name="nextVerificationDate" size="6" class=""
                          label="${message(code: 'dispatchRequest.nextVerificationDate.label', default: 'nextVerificationDate')}"
                          value="${dispatchRequest?.nextVerificationDate}"/>

            <el:integerField isDisabled="true" id="periodInMonths_label" name="periodInMonths_label" size="6" class=" isNumber"
                             label="${message(code: 'dispatchRequest.periodInMonths.label', default: 'periodInMonths')}"
                             value="${dispatchRequest?.periodInMonths}"/>

            <el:hiddenField id="periodInMonths" name="periodInMonths"
                             value="${dispatchRequest?.periodInMonths}"/>
        </el:formGroup>

        <el:formGroup>

            <el:dualAutocomplete
                    label="${message(code: 'dispatchRequest.organization.label', default: 'organizationId')}"
                    name="organizationId"
                    action="autocomplete"
                    controller="organization"
                    size="6"
                    values="${[[dispatchRequest?.organizationId, dispatchRequest?.transientData?.organizationDTO?.descriptionInfo?.localName]]}"
                    textName="organizationName"
                    textValue="${dispatchRequest?.organizationName}"/>

            <div id="educationMajorDiv" style="display: none;">
                <el:dualAutocomplete
                    label="${message(code: 'dispatchRequest.educationMajor.label', default: 'educationMajorId')}"
                    name="educationMajorId"
                    action="autocomplete"
                    controller="educationMajor"
                    size="6"
                    values="${[[dispatchRequest?.educationMajorId, dispatchRequest?.transientData?.educationMajorDTO?.descriptionInfo?.localName]]}"
                    textName="educationMajorName"
                    textValue="${dispatchRequest?.educationMajorName}"/>
            </div>

            <div id="trainingDiv" style="display: none;">
                <el:textField
                        name="trainingName"
                        id="trainingName"
                        size="6"
                        value="${dispatchRequest?.trainingName}"
                        label="${message(code: 'dispatchRequest.trainingName.label', default: 'trainingName')}"/>
            </div>


        </el:formGroup>
        <el:formGroup>
            <el:textArea name="requestStatusNote" size="6" class=""
                         label="${message(code: 'request.requestStatusNote.label', default: 'requestStatusNote')}"
                         value="${dispatchRequest?.requestStatusNote}"/>
        </el:formGroup>
    </lay:widgetBody>
</lay:widget>
<el:row/>

<lay:widget transparent="true" color="blue" icon="icon-location"
            title="${g.message(code: "dispatchRequest.location.label")}">
    <lay:widgetBody>
        <br/>
        <el:hiddenField name="locationId" value="${dispatchRequest?.locationId}"/>
        <g:render template="/pcore/location/wrapper"
                  model="[location                  : dispatchRequest?.transientData?.locationDTO,
                          isRegionRequired          : false,
                          isCountryRequired         : false,
                          showCountryWithOutRequired: true,
                          hiddenDetails             : true,
                          size                      : 6,
                          isDistrictRequired        : false]"/>
        <el:formGroup>
            <el:textArea name="unstructuredLocation" size="6" class=" "
                         label="${message(code: 'dispatchRequest.unstructuredLocation.label', default: 'unstructuredLocation')}"
                         value="${dispatchRequest?.unstructuredLocation}"/>
        </el:formGroup>



        <g:if test="${!hideManagerialOrderInfo}">
            <lay:widget transparent="true" color="green3" icon="fa fa-certificate" title="${g.message(code: "request.managerialOrderInfo.label")}">
                <lay:widgetBody>
                    <g:render template="/request/wrapperManagerialOrder" model="[request: dispatchRequest, formName:'dispatchRequestForm']"/>
                </lay:widgetBody>
            </lay:widget>
        </g:if>

        <g:if test="${workflowPathHeader}">
            <g:render template="/workflowPathDetails/form" model="[workflowPathHeader: workflowPathHeader]"/>
        </g:if>



    </lay:widgetBody>
</lay:widget>
<br/>

<script language="JavaScript">

    /*on change the toDate, calculate the  difference in months between fromDate, toDate and if difference is greater than 1 year set the nextVerificationDate value*/
    $("#toDate").change(function () {
        var toDate = $(this).val();
        var fromDate = $("#fromDate").val();
        if (toDate && fromDate) {
            var parts1 = fromDate.split("/");
            var date1 = new Date(parseInt(parts1[2], 10),
                parseInt(parts1[1], 10) - 1,
                parseInt(parts1[0], 10));

            var parts2 = toDate.split("/");
            var date2 = new Date(parseInt(parts2[2], 10),
                parseInt(parts2[1], 10) - 1,
                parseInt(parts2[0], 10));
            var diffInMonths = monthDiff(date1, date2);
            $("#periodInMonths").val(diffInMonths);
            $("#periodInMonths_label").val(diffInMonths);
            if (diffInMonths > 12) {
                var newDate = date1;
                newDate.setFullYear(newDate.getFullYear() + 1);
                var day = newDate.getDate();
                var month = newDate.getMonth() + 1;
                if (day < 10) {
                    day = "0" + day;
                }
                if (month < 10) {
                    month = "0" + month;
                }
                var dateFormatted = (day + '/' + month + '/' + newDate.getFullYear());
                $("#nextVerificationDate").val(dateFormatted)
            } else {
                $("#nextVerificationDate").val("");
            }
        }
    });

    /*calculate the months different between tow dates*/
    function monthDiff(d1, d2) {
        var months;
        months = (d2.getFullYear() - d1.getFullYear()) * 12;
        months -= d1.getMonth();
        months += d2.getMonth();
        return months <= 0 ? 0 : months;
    }


    $("#dispatchType").on("change", function (e) {
        if($("#dispatchType").val() == "${ps.gov.epsilon.hr.enums.dispatch.v1.EnumDispatchType.STUDY.toString()}"){
            $('#educationMajorDiv').show();
            $('#trainingDiv').hide();
            $('#trainingName').val();
        }else if($("#dispatchType").val() == "${ps.gov.epsilon.hr.enums.dispatch.v1.EnumDispatchType.TRAINING.toString()}"){
            $('#educationMajorDiv').hide();
            $('#trainingDiv').show();
            $('#educationMajorId').val("");
            $('#educationMajorName').val("");
        }else {
            $('#trainingName').val();
            $('#educationMajorId').val("");
            $('#educationMajorName').val("");
            $('#educationMajorDiv').hide();
            $('#trainingDiv').hide();
        }
    });

    <g:if test="${dispatchRequest?.dispatchType}">
        $("#dispatchType").trigger("change");
    </g:if>

</script>