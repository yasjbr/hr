<% def size = colSize ?: 6 %>

<g:if test="${size == 6}">

    <g:if test="${!hideInternalOrderInfo}">
        <el:formGroup>
            <el:dateField name="internalOrderDate" size="6" value="${request?.internalOrderDate}" isMaxDate="true"
                          onchange="resetInternalRequiredFields()"
                          label="${message(code: 'request.internalOrderDate.label', default: 'Internal Order Date')}"/>
            <el:textField name="internalOrderNumber" size="6" class="" value="${request?.internalOrderNumber}"
                          onchange="resetInternalRequiredFields()"
                          label="${message(code: 'request.internalOrderNumber.label', default: 'Internal Order Number')}"/>
        </el:formGroup>
    </g:if>

    <g:if test="${!hideExternalOrderInfo}">
        <g:if test="${handleOnChangeEvent}">
            <el:formGroup>
                <el:dateField name="externalOrderDate" size="6" onchange="externalOrderDateChange()"
                              value="${hideExternalOrderNumberValue ? null : request?.externalOrderDate}"
                              isMaxDate="true"
                              label="${message(code: 'request.externalOrderDate.label', default: 'External Order Date')}"/>
                <el:textField name="externalOrderNumber" size="6" class="" onchange="externalOrderNoChange()"
                              value="${hideExternalOrderNumberValue ? null : request?.externalOrderNumber}"
                              label="${message(code: 'request.externalOrderNumber.label', default: 'External Order Number')}"/>
            </el:formGroup>
        </g:if>
        <g:else>
            <el:formGroup>
                <el:dateField name="externalOrderDate" size="6"
                              value="${hideExternalOrderNumberValue ? null : request?.externalOrderDate}"
                              isMaxDate="true"
                              onchange="resetExternalRequiredFields()"
                              label="${message(code: 'request.externalOrderDate.label', default: 'External Order Date')}"/>
                <el:textField name="externalOrderNumber" size="6" class=""
                              value="${hideExternalOrderNumberValue ? null : request?.externalOrderNumber}"
                              onchange="resetExternalRequiredFields()"
                              label="${message(code: 'request.externalOrderNumber.label', default: 'External Order Number')}"/>
            </el:formGroup>
        </g:else>
    </g:if>
</g:if>
<g:else>
    <g:if test="${!hideInternalOrderInfo}">
        <el:formGroup>
            <el:dateField name="internalOrderDate" size="${size}" value="${request?.internalOrderDate}" isMaxDate="true"
                          onchange="resetInternalRequiredFields()"
                          label="${message(code: 'request.internalOrderDate.label', default: 'Internal Order Date')}"/>
        </el:formGroup>
        <el:formGroup>
            <el:textField name="internalOrderNumber" size="${size}" class="" value="${request?.internalOrderNumber}"
                          onchange="resetInternalRequiredFields()"
                          label="${message(code: 'request.internalOrderNumber.label', default: 'Internal Order Number')}"/>
        </el:formGroup>
    </g:if>
    <g:if test="${!hideExternalOrderInfo}">
        <el:formGroup>
            <el:dateField name="externalOrderDate" size="${size}"
                          value="${hideExternalOrderNumberValue ? null : request?.externalOrderDate}" isMaxDate="true"
                          onchange="resetExternalRequiredFields()"
                          label="${message(code: 'request.externalOrderDate.label', default: 'External Order Date')}"/>
        </el:formGroup>
        <el:formGroup>
            <el:textField name="externalOrderNumber" size="${size}" class=""
                          value="${hideExternalOrderNumberValue ? null : request?.externalOrderNumber}"
                          onchange="resetExternalRequiredFields()"
                          label="${message(code: 'request.externalOrderNumber.label', default: 'External Order Number')}"/>
        </el:formGroup>
    </g:if>
</g:else>
<g:render template="/request/requestDetails" model="[parentFolder: parentFolder]"/>

<script>

    function resetExternalRequiredFields() {
        if ('${formName}') {
            if ('${formName}') {
                if ($('#externalOrderDate').val() || $('#externalOrderNumber').val()) {
                    gui.formValidatable.addRequiredField('${formName}', 'externalOrderNumber');
                    gui.formValidatable.addRequiredField('${formName}', 'externalOrderDate');
                    $("#requestStatusId").show();
                } else {
                    gui.formValidatable.removeRequiredField('${formName}', 'externalOrderDate');
                    gui.formValidatable.removeRequiredField('${formName}', 'externalOrderNumber');
                    $("#requestStatusId").hide();
                    $("#acceptForm").hide(100);
                }
            }
        }
    }

    function resetInternalRequiredFields() {
        if ('${formName}') {
            if ('${formName}') {
                if ($('#internalOrderDate').val() || $('#internalOrderNumber').val()) {
                    gui.formValidatable.addRequiredField('${formName}', 'internalOrderNumber');
                    gui.formValidatable.addRequiredField('${formName}', 'internalOrderDate');
                } else {
                    gui.formValidatable.removeRequiredField('${formName}', 'internalOrderDate');
                    gui.formValidatable.removeRequiredField('${formName}', 'internalOrderNumber');
                }
            }
        }
    }

    /**
     * check if there is a duplication in request status, and remove it.
     */
    $(document).ready(function () {
        var element = document.getElementById("requestStatus");
        if (element) {
            $("#requestStatusId").remove();
        }
    });


</script>