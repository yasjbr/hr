<lay:widget transparent="true" color="blue" icon="icon-info-4" title="${g.message(code: "request.info.label")}">
    <lay:widgetBody>

        <msg:warning label="${message(code:'loanRequest.loanRequestWarning.label')}" />

        <el:formGroup>
            <el:autocomplete optionKey="id" optionValue="name" size="6" class=" isRequired" controller="job"
                             action="autocomplete" name="requestedJob.id"
                             label="${message(code:'loanRequest.requestedJob.label',default:'requestedJob')}"
                             values="${[[loanRequest?.requestedJob?.id,loanRequest?.requestedJob?.descriptionInfo?.localName]]}" />

            <el:textField name="requestedJobTitle" size="6"  class=""
                          label="${message(code:'loanRequest.requestedJobTitle.label',default:'requestedJobTitle')}"
                          value="${loanRequest?.requestedJobTitle}"/>
        </el:formGroup>


        <el:formGroup>
            <el:dateField name="fromDate"  size="6" class=" isRequired" setMinDateFor="toDate"
                          label="${message(code:'loanRequest.fromDate.label',default:'fromDate')}"
                          value="${loanRequest?.fromDate}" />

            <el:dateField name="toDate"  size="6" class=" isRequired"
                          label="${message(code:'loanRequest.toDate.label',default:'toDate')}"
                          value="${loanRequest?.toDate}" />

        </el:formGroup>

        <el:formGroup>

            <el:integerField  isReadOnly="true" name="periodInMonths" size="6"
                              class=" isRequired isNumber"
                              label="${message(code:'loanRequest.periodInMonths.label',default:'periodInMonths')}"
                              value="${loanRequest?.periodInMonths}" />

            <el:autocomplete optionKey="id" optionValue="name" size="6"
                             class="" controller="department" action="autocomplete"
                             name="toDepartment.id" label="${message(code:'loanRequest.toDepartment.label',default:'toDepartment')}"
                             values="${[[loanRequest?.toDepartment?.id,loanRequest?.toDepartment?.descriptionInfo?.localName]]}" />


        </el:formGroup>



        <el:formGroup>


            <el:integerField name="numberOfPositions" size="6"
                             class=" isRequired isNumber"
                             label="${message(code:'loanRequest.numberOfPositions.label',default:'numberOfPositions')}"
                             value="${loanRequest?.numberOfPositions}" />

            <el:autocomplete optionKey="id" optionValue="name" size="6" class=" "
                             paramsGenerateFunction="organizationParams"
                             controller="organization" action="autocomplete"
                             name="requestedFromOrganizationId"
                             label="${message(code:'loanRequest.requestedFromOrganizationId.label',default:'requestedFromOrganizationId')}"
                             values="${[[loanRequest?.requestedFromOrganizationId,
                                         loanRequest?.transientData?.requestedFromOrganizationDTO?.toString()]]}" />


        </el:formGroup>




        <el:formGroup>


            <el:textField name="requestReason" size="6"  class="" 
                          label="${message(code:'loanRequest.requestReason.label',default:'requestReason')}" 
                          value="${loanRequest?.requestReason}"/>
        </el:formGroup>

        <el:formGroup>

            <el:textArea name="description" size="6"  class=""
                         label="${message(code:'loanRequest.description.label',default:'description')}"
                         value="${loanRequest?.description}"/>
        </el:formGroup>







    </lay:widgetBody>

</lay:widget>


<lay:wall title="${g.message(code: 'loanRequest.loanRequestRelatedPerson.label')}">

    <div id="errorDiv" style="display: none;">
        <msg:error label="${message(code:'loanRequest.relatedPersonError.label')}" />
    </div>
    <br />
    <div id ="relatedPersonDiv">
        <el:formGroup>

                <el:autocomplete
                        optionKey="id"
                        optionValue="name"
                        size="8"
                        class=" isRequired"
                        controller="person"
                        action="autocomplete"
                        name="personId"
                        label="${message(code: 'employee.searchPerson.label', default: 'employee')}"/>

            <btn:addButton onclick="addRelatedPerson()"/>
        </el:formGroup>


        <label></label><ol id="relatedPersonUl" >
        <g:set var="loanRequestRelatedPersonsList" value="${loanRequest?.loanRequestRelatedPersons}" />
        <g:each in="${loanRequestRelatedPersonsList}" var="relatedPerson">
            <li id="relatedPerson_${relatedPerson?.requestedPersonId}" class="well-sm alert alert-success">
                <span id="relatedPerson_${relatedPerson?.requestedPersonId}_span">
                    ${relatedPerson?.transientData?.requestedPersonDTO}
                    <input type="hidden" value="${relatedPerson?.requestedPersonId}" name="requestedPersonId">
                </span>
                <button class='close' href='#' data-dismiss='alert'>X</button>
            </li>
        </g:each>
    </ol>

    </div>
</lay:wall>

<g:if test="${!hideManagerialOrderInfo}">
    <lay:widget transparent="true" color="green3" icon="fa fa-certificate" title="${g.message(code: "request.managerialOrderInfo.label")}">
        <lay:widgetBody>
            <g:render template="/request/wrapperManagerialOrder" model="[request: loanRequest, formName:'loanRequestForm',parentFolder:'loanList']"/>
        </lay:widgetBody>
    </lay:widget>
</g:if>

<g:if test="${workflowPathHeader}">
    <g:render template="/workflowPathDetails/form" model="[workflowPathHeader: workflowPathHeader]"/>
</g:if>


<script type="text/javascript">

    _currentValues = new Array();
    _spanIds = new Array();



    <g:if test="${loanRequest?.id && loanRequest.loanRequestRelatedPersons}">
    <g:each in="${loanRequestRelatedPersonsList}" var="relatedPerson">
    _currentValues.push("${relatedPerson?.transientData?.requestedPersonDTO}");
    _spanIds.push("relatedPerson_${relatedPerson?.requestedPersonId}_span");
    </g:each>
    </g:if>


    $(document).ready(function(){
        applyValidation("personId",false);
    });

    function changeRelatedPerson(){
        if($('#isJudgementForEver_').is(":checked")) {
            $("#relatedPersonDiv").hide();
            applyValidation("personId",false);
        }else{
            $("#relatedPersonDiv").show();
        }
    }


    function addRelatedPerson(){
        var personId = $.trim($("#personId").val());
        applyValidation("personId");
        var spanText = $('#personId').select2('data')[0].text;
        $("#relatedPersonUl .label-warning").removeClass("label-warning");
        $('#errorDiv').hide();

        var isValidRow = false;
        if(personId){
            isValidRow = true;
        }
        if (validation.isArrayContains.call(_currentValues, spanText)) {
            $('#errorDiv').show();
            var index = validation.indexOfArrayValue(_currentValues,spanText);
            var spanId = _spanIds[index];
            $('#'+spanId).parent().addClass("label-warning");

        }else if(isValidRow){
            var liId = "relatedPerson_"+personId;
            var spanTextId = liId+"_span";
            var newLi=$("<li id='"+liId+"'>");
            _currentValues.push(spanText);
            _spanIds.push(spanTextId);
            var indexArray = _spanIds.length - 1;
            newLi.append("<input type='hidden' name='requestedPersonId' value='"+personId+"'/>" );
            newLi.append("<span id='"+spanTextId+"' >"+spanText+"</span>");
            newLi.append("<button type='button' class='close' href='#' onclick='removeDetails("+indexArray+")'>X</button>");
            newLi.addClass("well-sm alert alert-success");
            $("#relatedPersonUl").prepend(newLi);
            applyValidation("personId",false);
            resetDetailsData();
        }
    }

    function removeDetails(indexArray) {
        var spanId = _spanIds[indexArray];
        var liId = spanId.replace("_span","");
        var spanText = $('#'+spanId).text();
        _currentValues = validation.removeFromArray(_currentValues,spanText);
        _spanIds = validation.removeFromArray(_spanIds,spanId);
        $('#'+liId).remove();
    }

    function resetDetailsData() {
        $("#personId").val('');
        $('#personId').trigger('change');
    }

    function applyValidation(inputId,isCheck) {
        if(isCheck == null)isCheck=true;
        var periodElement = $("#"+inputId);
        var periodElementParent = periodElement.closest(".pcp-form-control");
        if(!$.trim(periodElement.val()) && isCheck){
            periodElement.addClass("isRequired");
            periodElementParent.addClass("has-error");
        }else{
            periodElement.removeClass("isRequired");
            periodElementParent.removeClass("isRequired");
            periodElementParent.removeClass("has-error");
        }
    }

    function sendFirmData(){
        return {"firm.id":"${ps.police.common.utils.v1.PCPSessionUtils.getValue("firmId")}"};
    }


    function organizationParams() {
        return {
            "organizationType.id":"${ps.police.pcore.enums.v1.OrganizationTypeEnum.SECURITY_FORCES.value()}"
        };
    }

    /*on change the toDate, calculate the  difference in months between fromDate, toDate and if difference is greater than 1 year set the nextVerificationDate value*/
    $("#fromDate").change(function () {
        setPeriodValue();
    });
    $("#toDate").change(function () {
        setPeriodValue();
    });

    function setPeriodValue(){
        var fromDate = $("#fromDate").val();
        var toDate = $("#toDate").val();
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
        }
    }

    /*calculate the months different between tow dates*/
    function monthDiff(d1, d2) {
        var months;
        months = (d2.getFullYear() - d1.getFullYear()) * 12;
        months -= d1.getMonth();
        months += d2.getMonth();
        return months <= 0 ? 0 : months;
    }


</script>
