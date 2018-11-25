<g:render template="/DescriptionInfo/wrapper" model="[bean:disciplinaryJudgment?.descriptionInfo]" />


<el:formGroup>
    <el:checkboxField label="${message(code:'disciplinaryJudgment.hasValidity.label')}" size="8" name="hasValidity"
                      isChecked="${disciplinaryJudgment?.hasValidity}" />
</el:formGroup>


<el:formGroup>
    <el:checkboxField label="${message(code:'disciplinaryJudgment.isCurrencyUnit.label')}" size="8" name="isCurrencyUnit" isChecked="${disciplinaryJudgment?.isCurrencyUnit}" value="${disciplinaryJudgment?.isCurrencyUnit}" onchange="manageUnit()"/>
</el:formGroup>

<el:formGroup id="unitIdsFormGroup">
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="8"
                     class=" "
                     controller="unitOfMeasurement"
                     action="autocomplete"
                     name="unitIds"
                     label="${message(code: 'disciplinaryJudgment.unitIds.label', default: 'unitIds')}"
                     values="${disciplinaryJudgment?.transientData?.unitOfMeasurementList}"
                     multiple="true"/>
</el:formGroup>

<el:formGroup id="currencyIdsFormGroup">
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="8"
                     class=" "
                     controller="currency"
                     action="autocomplete"
                     name="currencyIds"
                     label="${message(code: 'disciplinaryJudgment.currencyIds.label', default: 'currencyIds')}"
                     values="${disciplinaryJudgment?.transientData?.currencyList}"
                     multiple="true"/>
</el:formGroup>


<el:formGroup>
    <el:dualListBox size="8" optionKey="id"
                    from="${disciplinaryReasonList}"
                    values="${disciplinaryJudgment?.joinedDisciplinaryJudgmentReasons?.disciplinaryReason}"
                    label="${message(code: 'disciplinaryReason.label', default: 'joinedDisciplinaryJudgmentReasons')}"
                    name="judgmentReasons.id" moveOnSelect="false"
                    showFilterInputs="true"
                    isAllowToAdd="true"
    />
</el:formGroup>

<el:formGroup>
    <el:checkboxField label="${message(code:'disciplinaryJudgment.excludedFromEligiblePromotion.label',default:'universalCode')}" size="8" name="excludedFromEligiblePromotion" isChecked="${disciplinaryJudgment.excludedFromEligiblePromotion}" value="${disciplinaryJudgment.excludedFromEligiblePromotion}" />
</el:formGroup>

<el:formGroup>
    <el:textField name="universalCode" size="8"  class="" label="${message(code:'disciplinaryJudgment.universalCode.label',default:'universalCode')}" value="${disciplinaryJudgment?.universalCode}"/>
</el:formGroup>


<script>
    function manageUnit(){
        var input = $('#isCurrencyUnit_');
        if(input.is(":checked")){
            $('#currencyIdsFormGroup').show();
            $('#currencyIds').removeAttr("disabled");
            $('#unitIdsFormGroup').hide();
            $('#unitIds').attr("disabled",'true');
        }else{
            $('#currencyIdsFormGroup').hide();
            $('#currencyIds').attr("disabled",'true');
            $('#unitIdsFormGroup').show();
            $('#unitIds').removeAttr("disabled");
        }
        $('#currencyIds').trigger('change');
        $('#unitIds').trigger("change");
    }
    manageUnit();
</script>