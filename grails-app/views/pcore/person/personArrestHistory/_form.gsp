
<g:render template="/pcore/person/wrapper" model="[id:(personCallBackId?:'personId'),
                                                   isHiddenInfo:params.isHiddenPersonInfo,
                                                   name:'person.id',
                                                   messageValue:message(code:'personArrestHistory.employee.label',default:'employee'),
                                                   bean:personArrestHistory?.person,
                                                   isDisabled:isPersonDisabled]" />
<el:formGroup>
    <el:select valueMessagePrefix="ArrestingClassification"
               from="${ps.police.pcore.enums.v1.ArrestingClassification.values()}"
               name="arrestingClassification" size="8"  class=" isRequired"
               label="${message(code:'personArrestHistory.arrestingClassification.label',default:'arrestingClassification')}"
               value="${personArrestHistory?.arrestingClassification?.toString()?:ps.police.pcore.enums.v1.ArrestingClassification.SECURITY.toString()}" />
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="ArrestingParty"  from="${ps.police.pcore.enums.v1.ArrestingParty.values()}"
               name="arrestingParty" size="8"  class=" isRequired"
               label="${message(code:'personArrestHistory.arrestingParty.label',default:'arrestingParty')}"
               value="${personArrestHistory?.arrestingParty?.toString()?:ps.police.pcore.enums.v1.ArrestingParty.ISREAL.toString()}" />
</el:formGroup>

<el:formGroup>
    <el:dateField name="arrestDate"  size="8" class=" isRequired" setMinDateFor="releaseDate" isMaxDate="true"
                  label="${message(code:'personArrestHistory.arrestDate.label',default:'arrestDate')}" value="${personArrestHistory?.arrestDate}" />
</el:formGroup>

<el:formGroup>
    <el:dateField name="releaseDate"  size="8" class=" " isMaxDate="true" label="${message(code:'personArrestHistory.releaseDate.label',default:'releaseDate')}" value="${personArrestHistory?.releaseDate}" />
</el:formGroup>

<el:formGroup>
    <el:integerField id="periodInMonths" name="periodInMonths" size="8" class=" isNumber"
                     label="${message(code: 'personArrestHistory.periodInMonths.label', default: 'periodInMonths')}"
                     value="${personArrestHistory?.periodInMonths}"/>
</el:formGroup>

<el:formGroup>
    <el:textField name="accusation" size="8"  class=" " label="${message(code:'personArrestHistory.accusation.label',default:'accusation')}" value="${personArrestHistory?.accusation}"/>
</el:formGroup>


<el:formGroup>
    <el:textField name="arrestReason" size="8"  class="" label="${message(code:'personArrestHistory.arrestReason.label',default:'arrestReason')}" value="${personArrestHistory?.arrestReason}"/>
</el:formGroup>

<el:formGroup>
    <el:dualAutocomplete label="${message(code:'personArrestHistory.jail.label',default:'jail')}"
                         name="jail.id" action="autocomplete"
                         controller="jail" size="8"
                         values="${[[personArrestHistory?.jail?.id,personArrestHistory?.jail?.descriptionInfo?.localName]]}"
                         textName="jailName"
                         textValue="${personArrestHistory?.jailName}"
    />
</el:formGroup>


<el:formGroup>
    <el:textField name="lawyerName" size="8"  class="" label="${message(code:'personArrestHistory.lawyerName.label',default:'lawyerName')}" value="${personArrestHistory?.lawyerName}"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="note" size="8"  class="" label="${message(code:'personArrestHistory.note.label',default:'note')}" value="${personArrestHistory?.note}"/>
</el:formGroup>



<lay:wall title="${g.message(code: 'personArrestHistory.arrestPeriod.label')}">

    <div id="errorDiv" style="display: none;">
        <msg:error label="${message(code:'personArrestHistory.arrestDetailsError.label')}" />
    </div>
    <el:formGroup>
        <el:checkboxField name="isJudgementForEver" size="8" class=""
                          label="${message(code: 'personArrestHistory.isJudgementForEver.label', default: 'isJudgementForEver')}"
                          value="${personArrestHistory?.isJudgementForEver}"
                          isChecked="${personArrestHistory?.isJudgementForEver}"
                          onchange="changeArrestPeriod()"
                          isDisabled="false"/>
    </el:formGroup>
    <div id ="arrestPeriodDiv">
        <el:formGroup>
            <el:select valueMessagePrefix="ArrestJudgementType"
                       from="${ps.police.pcore.enums.v1.ArrestJudgementType.values()}"
                       name="arrestJudgementTypeTemp" size="4" class=" isRequired"
                       label="${message(code: 'arrestJudgementDetails.arrestJudgementType.label', default: 'arrestJudgementType')}"/>


            <div class="col-xs-12 col-sm-12 col-md-6 pcp-form-control">

                <label id="judgementLabel" class="col-xs-2 col-sm-2 col-md-2 control-label no-padding-right text-left" >
                    ${message(code: 'arrestJudgementDetails.arrestPeriod.label', default: 'arrestPeriod')}
                </label>


                <div class="col-xs-5 col-sm-5 col-md-5">


                    <input type="text" class="form-control isRequired input-integer col-md-4"
                           data-placement="bottom" id="arrestPeriodTemp" name="arrestPeriodTemp" />
                </div>

                <div id="unitOfMeasurementTempDiv" class="col-xs-5 col-sm-5 col-md-5" style="margin-right: -24px">

                    <g:select icon="icon-search" customOptionKey="id" customOptionValue="name"
                              ajaxUrl="${createLink(controller: 'unitOfMeasurement',action: 'autocomplete')}"
                              class="select2-remote form-control col-md-4" from="${[]}"
                              name="unitOfMeasurementTemp" isMultiple="false" preventSpaces="false"
                              paramsGenerateFunction="unitOfMeasurementParam" />
                </div>




            </div>



            <btn:createButton onclick="addArrestPeriod()"/>
        </el:formGroup>
    </div>


        <label></label><ol id="periodDetailsUl" >
        <g:set var="arrestJudgementDetailsList" value="${personArrestHistory.arrestJudgementDetails?.toList()?.sort{it.arrestPeriod}}" />
        <g:each in="${arrestJudgementDetailsList}" var="arrestJudgementDetail">
            <li id="judgementType_${arrestJudgementDetail?.unitOfMeasurement?.id}_${arrestJudgementDetail?.arrestPeriod}" class="well-sm alert alert-success">
                <span id="judgementType_${arrestJudgementDetail?.unitOfMeasurement?.id}_${arrestJudgementDetail?.arrestPeriod}_span">
                    <input type="hidden" value="${arrestJudgementDetail?.arrestJudgementType}" name="arrestJudgementType">
                    <input type="hidden" value="${arrestJudgementDetail?.unitOfMeasurement?.id}" name="unitOfMeasurement.id">
                    <input type="hidden" value="${arrestJudgementDetail?.arrestPeriod}" name="arrestPeriod">

                    <g:if test="${arrestJudgementDetail?.unitOfMeasurement}">
                        ${arrestJudgementDetail?.arrestPeriod} - ${arrestJudgementDetail?.unitOfMeasurement?.toString()}
                    </g:if>
                    <g:else>
                        ${arrestJudgementDetail?.arrestPeriod} - ${g.message(code: "ArrestJudgementType."+arrestJudgementDetail?.arrestJudgementType)}
                    </g:else>
                </span><button class='close' href='#' data-dismiss='alert'>X</button>
            </li>
        </g:each>
    </ol>

    </div>
</lay:wall>



<script type="text/javascript">

    _currentValues = new Array();
    _spanIds = new Array();

    <g:if test="${personArrestHistory?.id && personArrestHistory.arrestJudgementDetails}">
    <g:each in="${arrestJudgementDetailsList}" var="arrestJudgementDetail">
    <g:if test="${arrestJudgementDetail?.unitOfMeasurement}">
    _currentValues.push("${arrestJudgementDetail?.arrestPeriod} - ${arrestJudgementDetail?.unitOfMeasurement?.toString()}");
    </g:if>
    <g:else>
    _currentValues.push("${arrestJudgementDetail?.arrestPeriod} - ${g.message(code: "ArrestJudgementType."+arrestJudgementDetail?.arrestJudgementType)}");
    </g:else>
    _spanIds.push("judgementType_${arrestJudgementDetail?.unitOfMeasurement?.id}_${arrestJudgementDetail?.arrestPeriod}_span");
    </g:each>
    </g:if>


    $(document).ready(function(){
        applyValidation("arrestJudgementTypeTemp",false);
        applyValidation("arrestPeriodTemp",false);
    });

    function changeArrestPeriod(){
        if($('#isJudgementForEver_').is(":checked")) {
            $("#arrestPeriodDiv").hide();
            applyValidation("arrestJudgementTypeTemp",false);
            applyValidation("arrestPeriodTemp",false);
        }else{
            $("#arrestPeriodDiv").show();
        }
    }


    function addArrestPeriod(){
        var judgementType      =  $.trim($("#arrestJudgementTypeTemp").val());
        var unitOfMeasurement  =  $.trim($("#unitOfMeasurementTemp").val());
        var arrestPeriod       =  $.trim($("#arrestPeriodTemp").val());
        applyValidation("arrestJudgementTypeTemp");
        applyValidation("arrestPeriodTemp");
        if(judgementType == '${ps.police.pcore.enums.v1.ArrestJudgementType.PERIOD}') {
            applyValidation("unitOfMeasurementTemp");
        }
        var spanText = "";
        if($('#unitOfMeasurementTemp').data("select2") && !$('#unitOfMeasurementTemp').attr("disabled")) {
            spanText += arrestPeriod+" - "+$('#unitOfMeasurementTemp').select2('data')[0].text;
        }else{
            spanText += arrestPeriod+ " - " +$("#arrestJudgementTypeTemp option:selected").text();
        }
        $("#periodDetailsUl .label-warning").removeClass("label-warning");
        $('#errorDiv').hide();

        var isValidRow = false;
        if(judgementType == '${ps.police.pcore.enums.v1.ArrestJudgementType.EVER_LASTING}' && arrestPeriod){
            isValidRow = true;
        }
        if(judgementType == '${ps.police.pcore.enums.v1.ArrestJudgementType.PERIOD}' && arrestPeriod && unitOfMeasurement){
            isValidRow = true;
        }
        if (validation.isArrayContains.call(_currentValues, spanText)) {
            var index = validation.indexOfArrayValue(_currentValues,spanText);
            var spanId = _spanIds[index];
            $('#'+spanId).parent().addClass("label-warning");
            $('#errorDiv').show();
        }else if(isValidRow){
            var liId = "judgementType_"+"_"+unitOfMeasurement+"_"+arrestPeriod;
            var spanTextId = liId+"_span";
            var newLi=$("<li id='"+liId+"'>");
            _currentValues.push(spanText);
            _spanIds.push(spanTextId);
            var indexArray = _spanIds.length - 1;
            newLi.append("<input type='hidden' name='arrestJudgementType' value='"+judgementType+"'/>" );
            newLi.append("<input type='hidden' name='unitOfMeasurement.id' value='"+unitOfMeasurement+"'/>");
            newLi.append("<input type='hidden' name='arrestPeriod' value='"+arrestPeriod+"'/>");
            newLi.append("<span id='"+spanTextId+"' >"+spanText+"</span>");
            newLi.append("<button type='button' class='close' href='#' onclick='removeDetails("+indexArray+")'>X</button>");
            newLi.addClass("well-sm alert alert-success");
            $("#periodDetailsUl").prepend(newLi);
            applyValidation("arrestJudgementTypeTemp",false);
            applyValidation("arrestPeriodTemp",false);
            applyValidation("unitOfMeasurementTemp",false);
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
        $("#arrestPeriodTemp").val('');
        $("#arrestJudgementTypeTemp").val('');
        $("#unitOfMeasurementTemp").val('');
        $('#unitOfMeasurementTemp').trigger('change');
        $("#arrestJudgementTypeTemp").trigger('chosen:updated');
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


    $("#arrestJudgementTypeTemp").change(function(){
        var selectedVal =   $.trim($("#arrestJudgementTypeTemp").val());
        if(selectedVal == '${ps.police.pcore.enums.v1.ArrestJudgementType.EVER_LASTING}'){
            $("#unitOfMeasurementTemp").attr("disabled",true);
            $('#unitOfMeasurementTempDiv').hide();
            gui.autocomplete.clear("unitOfMeasurementTemp");
            $('#judgementLabel').html("${message(code:'arrestJudgementDetails.arrestPeriodEver.label')}");
        }else{
            $("#unitOfMeasurementTemp").attr("disabled",false);
            $('#unitOfMeasurementTempDiv').show();
            $('#judgementLabel').html("${message(code:'arrestJudgementDetails.arrestPeriod.label')}");
        }
    });

    function unitOfMeasurementParam() {
        return {'unitCategory.id': '${ps.police.pcore.enums.v1.UnitCategory.TIME.value()}'}
    }

    /*on change the toDate, calculate the  difference in months between fromDate, toDate and if difference is greater than 1 year set the nextVerificationDate value*/
    $("#releaseDate").change(function () {
        setPeriodValue();
    });
    $("#arrestDate").change(function () {
        setPeriodValue();
    });

    function setPeriodValue(){
        var arrestDate = $("#arrestDate").val();
        var releaseDate = $("#releaseDate").val();

        if (arrestDate && releaseDate) {
            var parts1 = arrestDate.split("/");
            var date1 = new Date(parseInt(parts1[2], 10),
                parseInt(parts1[1], 10) - 1,
                parseInt(parts1[0], 10));

            var parts2 = releaseDate.split("/");
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
