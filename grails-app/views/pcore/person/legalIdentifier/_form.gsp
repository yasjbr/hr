<lay:wall title="${g.message(code: 'legalIdentifier.owner.label')}" style="${params.isHiddenPersonInfo == "true"?("display: none;"):""}">
    <el:formGroup>
        <g:if test="${isDocumentOwnerDisabled}">
            <el:hiddenField name="documentOwner" value="${legalIdentifier?.documentOwner}" />
        </g:if>
        <el:select valueMessagePrefix="RelatedParty"
                   from="${ps.police.pcore.enums.v1.RelatedParty.values()}"
                   name="documentOwner" size="8"  class=" isRequired"
                   label="${message(code:'legalIdentifier.documentOwner.label',default:'documentOwner')}"
                   value="${legalIdentifier?.documentOwner?.toString()}"
                   disabled="${isDocumentOwnerDisabled?'true':'false'}"/>
    </el:formGroup>



    <g:render template="/pcore/organization/wrapper" model="[id:(organizationCallBackId?:'ownerOrganizationId'),
                                                       name:'ownerOrganization.id',
                                                       bean:legalIdentifier?.ownerOrganization,
                                                       isDisabled:isOrganizationDisabled]" />

    <g:render template="/pcore/person/wrapper" model="[id:(ownerPersonCallBackId?:'ownerPersonId'),
                                                 name:'ownerPerson.id',
                                                 bean:legalIdentifier?.ownerPerson,
                                                 isDisabled:isPersonDisabled]" />
</lay:wall>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired"
                     controller="documentType" action="autocomplete"
                     name="documentType.id" id="documentTypeId"
                     label="${message(code:'legalIdentifier.documentType.label',
                             default:'documentType')}"
                     values="${[[legalIdentifier?.documentTypeClassification?.documentType?.id,
                                 legalIdentifier?.documentTypeClassification?.documentType?.descriptionInfo?.localName]]}" />
</el:formGroup>

<el:formGroup>

    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired"  paramsGenerateFunction="documentTypeParams"
                     controller="documentClassification" action="autocomplete" name="documentClassification.id" id="documentClassificationId"
                     label="${message(code:'legalIdentifier.documentClassification.label',default:'documentClassification')}"
                     values="${[[legalIdentifier?.documentTypeClassification?.documentClassification?.id,
                                 legalIdentifier?.documentTypeClassification?.documentClassification?.descriptionInfo?.localName]]}" />
</el:formGroup>



<el:formGroup>
    <el:textField name="documentNumber" size="8"  class=" isRequired" label="${message(code:'legalIdentifier.documentNumber.label',default:'documentNumber')}" value="${legalIdentifier?.documentNumber}"/>
</el:formGroup>

<el:formGroup>
    <el:dateField name="issuingDate"  size="8" class=" " label="${message(code:'legalIdentifier.issuingDate.label',default:'issuingDate')}" value="${legalIdentifier?.issuingDate}" />
</el:formGroup>

<el:formGroup>
    <el:dateField name="validFrom"  size="8" class=" " label="${message(code:'legalIdentifier.validFrom.label',default:'validFrom')}" value="${legalIdentifier?.validFrom}" />
</el:formGroup>

<el:formGroup>
    <el:dateField name="validTo"  size="8" class=" " label="${message(code:'legalIdentifier.validTo.label',default:'validTo')}" value="${legalIdentifier?.validTo}" />
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" " controller="organization"
                     action="autocomplete" name="issuedByOrganization.id"
                     label="${message(code:'legalIdentifier.issuedByOrganization.label',default:'issuedByOrganization')}"
                     values="${[[legalIdentifier?.issuedByOrganization?.id,
                                 legalIdentifier?.issuedByOrganization?.descriptionInfo?.localName]]}" />
</el:formGroup>





<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=""
                     controller="legalIdentifierLevel" action="autocomplete" name="legalIdentifierLevel.id"
                     label="${message(code:'legalIdentifier.legalIdentifierLevel.label',default:'legalIdentifierLevel')}"
                     values="${[[legalIdentifier?.legalIdentifierLevel?.id,
                                 legalIdentifier?.legalIdentifierLevel?.descriptionInfo?.localName]]}" />
</el:formGroup>


<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=""
                     controller="country" action="autocomplete" name="countryOfOrigin.id"
                     label="${message(code:'legalIdentifier.countryOfOrigin.label',default:'countryOfOrigin')}"
                     values="${[[legalIdentifier?.countryOfOrigin?.id,
                                 legalIdentifier?.countryOfOrigin?.descriptionInfo?.localName]]}" />
</el:formGroup>

<el:formGroup>
    <el:textField name="note" size="8"  class="" label="${message(code:'legalIdentifier.note.label',default:'note')}" value="${legalIdentifier?.note}"/>
</el:formGroup>


<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" multiple="true" class=""
                     controller="legalIdentifierRestriction" action="autocomplete"
                     name="legalIdentifierRelatedRestrictions.id"
                     label="${message(code:'legalIdentifier.legalIdentifierRelatedRestrictions.label',
                             default:'legalIdentifierRelatedRestrictions')}"
                     values="${legalIdentifier?.legalIdentifierRelatedRestrictions?.legalIdentifierRestriction?.collect{return [it?.id,it?.descriptionInfo?.localName]}}" />
</el:formGroup>
<el:formGroup class="updateProfileValueFormGroup">
    <el:checkboxField name="updateProfileValue" id ="updateProfileValue" size="8" class=""
                      label="${message(code: 'legalIdentifier.updateProfileValue.label', default: 'updateProfileValue')}"
                      value="false" isChecked="false"/>
</el:formGroup>

<lay:wall title="${g.message(code: 'legalIdentifier.issueLocation.label')}">
    <g:render template="/pcore/location/wrapper"
              model="[
                      isCountryRequired         : true,
                      hiddenDetails             : true,
                      size                      : 8,
                      location            :legalIdentifier?.issueLocation]" />

    <el:formGroup>
        <el:textArea name="unstructuredIssueLocation" size="8"  class=" " label="${message(code:'location.unstructuredLocation.label',default:'unstructuredLocation')}" value="${legalIdentifier?.unstructuredIssueLocation}"/>
    </el:formGroup>
</lay:wall>

<script>

    var documentTypeList = [
        '${ps.police.pcore.enums.v1.DocumentType.IDENTITY_CARDEN.value()}',
        '${ps.police.pcore.enums.v1.DocumentType.PASSPORT.value()}'];
    $("#documentOwner").change(function () {
        if ($(this).val() == '${ps.police.pcore.enums.v1.RelatedParty.ORGANIZATION}') {
            gui.formValidatable.addRequiredField('contactInfoForm',"${organizationCallBackId?:'organizationId'}");
            gui.formValidatable.removeRequiredField('contactInfoForm',"${personCallBackId?:'personId'}");
            $("#organizationFormGroup").show();
            $("#personFormGroup").hide();
        } else if ($(this).val() == '${ps.police.pcore.enums.v1.RelatedParty.PERSON}') {
            gui.formValidatable.removeRequiredField('contactInfoForm',"${organizationCallBackId?:'organizationId'}");
            gui.formValidatable.addRequiredField('contactInfoForm',"${personCallBackId?:'personId'}");
            $("#organizationFormGroup").hide();
            $("#personFormGroup").show();
        } else {
            gui.formValidatable.removeRequiredField('contactInfoForm',"${organizationCallBackId?:'organizationId'}");
            gui.formValidatable.removeRequiredField('contactInfoForm',"${personCallBackId?:'personId'}");
            $("#organizationFormGroup").hide();
            $("#personFormGroup").hide();
        }
    });




    $("#documentTypeId").change(function () {

        var selectedValue = $("#documentTypeId").val();
        if($.inArray(selectedValue,documentTypeList) > -1) {
            $("#updateProfileValue").val('');
            $(".updateProfileValueFormGroup").show();
        }else{
            $(".updateProfileValueFormGroup").hide();
        }

        gui.autocomplete.clear("documentClassificationId");
    });


    $(document).ready(function () {
        $("#documentOwner").trigger("change");
        $("#documentTypeId").trigger("change");

    });

    function documentTypeParams() {
        var documentTypeId = $('#documentTypeId').val();
        return {'documentType.id': documentTypeId}
    }
</script>
