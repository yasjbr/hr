<el:hiddenField name="employee.id" value="${employmentServiceRequest?.employee?.id}"/>
<el:hiddenField name="requestType" value="${ps.gov.epsilon.hr.enums.v1.EnumRequestType.END_OF_SERVICE}"/>
<g:render template="/employee/wrapperForm" model="[employee: employmentServiceRequest?.employee]"/>
<lay:widget transparent="true" color="blue" icon="icon-info-4" title="${g.message(code: "request.info.label")}">
    <lay:widgetBody>
        <br/>
        <el:formGroup>
            <el:dateField name="requestDate" size="6" class=" isRequired"
                          label="${message(code: 'employmentServiceRequest.requestDate.label', default: 'requestDate')}"
                          value="${employmentServiceRequest?.requestDate}"/>
        </el:formGroup>
        <el:formGroup>
            <el:autocomplete optionKey="id"
                             optionValue="name"
                             size="6"
                             class=" isRequired"
                             controller="serviceActionReasonType"
                             action="autocomplete"
                             paramsGenerateFunction="typeParams"
                             id="serviceActionReasonTypeId"
                             name="serviceActionReasonType.id"
                             label="${message(code: 'endOfService.serviceActionReasonType.label', default: 'serviceActionReasonType')}"
                             values="${[[employmentServiceRequest?.serviceActionReason?.serviceActionReasonType?.id, employmentServiceRequest?.serviceActionReason?.serviceActionReasonType?.descriptionInfo?.localName]]}"/>


            <el:autocomplete optionKey="id"
                             optionValue="name"
                             size="6"
                             class=" isRequired"
                             paramsGenerateFunction="reasonParams"
                             controller="serviceActionReason"
                             action="autocomplete"
                             id="serviceActionReasonId"
                             name="serviceActionReason.id"
                             label="${message(code: 'endOfService.serviceActionReason.label', default: 'serviceActionReason')}"
                             values="${[[employmentServiceRequest?.serviceActionReason?.id, employmentServiceRequest?.serviceActionReason?.descriptionInfo?.localName]]}"/>

        </el:formGroup>
        <g:if test="${isHRApplication}">
            <el:formGroup>
                <el:dateField name="expectedDateEffective" id="expectedDateEffective" size="6" class=""
                              onchange="resetRequierdFields()"
                              label="${message(code: 'employmentServiceRequest.expectedDateEffective.label', default: 'expectedDateEffective')}"
                              value="${employmentServiceRequest?.expectedDateEffective}"/>
                <el:textArea name="requestStatusNote" size="6" class=""
                             label="${message(code: 'employmentServiceRequest.requestStatusNote.label', default: 'requestStatusNote')}"
                             value="${employmentServiceRequest?.requestStatusNote}" />
            </el:formGroup>
        </g:if>
        <g:else>
            <el:formGroup>
                <el:textArea name="requestStatusNote" size="6" class=""
                             label="${message(code: 'employmentServiceRequest.requestStatusNote.label', default: 'requestStatusNote')}"
                             value="${employmentServiceRequest?.requestStatusNote}" />
            </el:formGroup>
        </g:else>


        <g:if test="${!hideManagerialOrderInfo}">
            <lay:widget transparent="true" color="green3" icon="fa fa-certificate" title="${g.message(code: "request.managerialOrderInfo.label")}">
                <lay:widgetBody>
                    <g:render template="/request/wrapperManagerialOrder" model="[request:employmentServiceRequest, handleOnChangeEvent:true, formName:'employmentServiceRequestForm',parentFolder:'serviceList']"/>
                </lay:widgetBody>
            </lay:widget>
        </g:if>


        <g:if test="${workflowPathHeader}">
            <g:render template="/workflowPathDetails/form" model="[workflowPathHeader: workflowPathHeader]"/>
        </g:if>

    </lay:widgetBody>
</lay:widget>


<script type="text/javascript">
    function reasonParams() {
        return {
            "serviceActionReasonType.id": $('#serviceActionReasonTypeId').val(),
            "firm.id": "${ps.police.common.utils.v1.PCPSessionUtils.getValue("firmId")}",
            "isRelatedToEndOfService_string": "YES"
        };
    }

    function typeParams() {
        return {
            "isRelatedToEndOfService_string": "YES",
            "firm.id": "${ps.police.common.utils.v1.PCPSessionUtils.getValue("firmId")}"
        };
    }

    $("#serviceActionReasonTypeId").on("select2:close", function (e) {
        var value = $('#serviceActionReasonTypeId').val();
        if (value) {
            $('#serviceActionReasonId').val("");
            $('#serviceActionReasonId').trigger('change');
        }
    });

    $("#serviceActionReasonId").on("select2:close", function (e) {
        var value = $('#serviceActionReasonId').val();
        var typeValue = $('#serviceActionReasonTypeId').val();
        if (value && (!typeValue || typeValue == "")) {
            $.ajax({
                url: '${createLink(controller: 'serviceActionReason',action: 'getServiceActionReasonType')}',
                type: 'POST',
                data: {
                    id: value,
                    "isRelatedToEndOfService_string": "YES"
                },
                dataType: 'json',
                beforeSend: function (jqXHR, settings) {
                    guiLoading.show();
                },
                error: function (jqXHR) {
                    guiLoading.hide();
                },
                success: function (json) {
                    guiLoading.hide();
                    if (json) {
                        $("#serviceActionReasonTypeId").val(json.id);
                        var newOption = new Option(json.descriptionInfo.localName, json.id, true, true);
                        $('#serviceActionReasonTypeId').append(newOption);
                        $('#serviceActionReasonTypeId').trigger('change');
                    }
                }
            });
        }
    });

    function externalOrderDateChange() {
        resetRequierdFields();
    }

    function externalOrderNoChange() {
        resetRequierdFields();
    }

    function resetRequierdFields() {
        if ($('#expectedDateEffective').val() || $('#externalOrderDate').val() || $('#externalOrderNumber').val()) {
            gui.formValidatable.addRequiredField('employmentServiceRequestForm','expectedDateEffective');
            gui.formValidatable.addRequiredField('employmentServiceRequestForm','externalOrderNumber');
            gui.formValidatable.addRequiredField('employmentServiceRequestForm','externalOrderDate');
        } else {
            gui.formValidatable.removeRequiredField('employmentServiceRequestForm','expectedDateEffective');
            gui.formValidatable.removeRequiredField('employmentServiceRequestForm','externalOrderDate');
            gui.formValidatable.removeRequiredField('employmentServiceRequestForm','externalOrderNumber');
        }
    }
</script>