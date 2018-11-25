<g:if test="${withPhoneNumber == true}">
    <el:formGroup>
        <g:if test="${!isRequiredPhoneNumber}">
            <el:textField name="phoneNumber" size="8" class=" "
                          label="${message(code: 'bordersSecurityCoordination.phone.label', default: 'phone')}"
                          value=""/>
        </g:if><g:else>
        <el:textField name="phoneNumber" size="8" class=" isRequired"
                      label="${message(code: 'bordersSecurityCoordination.phone.label', default: 'phone')}"
                      value=""/>
    </g:else>
    </el:formGroup>
</g:if>

<g:set var="messagePrefix" value="${params?.justAddress == 'true' ? "contactInfoAddress" : "contactInfo"}"/>
<lay:wall title="${g.message(code: messagePrefix + '.relatedType.label')}"
          style="${params.isHiddenPersonInfo == "true" ? ("display: none;") : ""}">
    <el:formGroup>
        <g:if test="${isRelatedObjectTypeDisabled}">
            <el:hiddenField name="relatedObjectType" value="${contactInfo?.relatedObjectType}"/>
        </g:if>

        <el:select valueMessagePrefix="ContactInfoClassification"
                   from="${ps.police.pcore.enums.v1.ContactInfoClassification.values()}"
                   name="relatedObjectType" size="8" class=" isRequired"
                   label="${message(code: messagePrefix + '.relatedObjectType.label', default: 'relatedObjectType')}"
                   value="${contactInfo?.relatedObjectType?.toString()}"
                   disabled="${isRelatedObjectTypeDisabled ? 'true' : 'false'}"/>
    </el:formGroup>

    <g:render template="/pcore/organization/wrapper" model="[id        : (organizationCallBackId ?: 'organizationId'),
                                                             name      : 'organization.id',
                                                             bean      : contactInfo?.organization,
                                                             isRequired: false,
                                                             isDisabled: isOrganizationDisabled]"/>

    <g:render template="/pcore/person/wrapper"
              model="[id        : (personCallBackId ?: 'personId'),
                      name      : 'person.id',
                      bean      : contactInfo?.person,
                      isSearch  : true,
                      isDisabled: isPersonDisabled]"/>

</lay:wall>


<el:formGroup style="${hideDetails ? "display: none;" : ''}">
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="contactType"
                     action="autocomplete" name="contactType.id" isDisabled="${params.isDisabled ? true : false}"
                     id="contactTypeId"
                     label="${message(code: messagePrefix + '.contactType.label', default: 'contactType')}"
                     values="${[[contactInfo?.contactType?.id, contactInfo?.contactType?.descriptionInfo?.localName]]}"/>
</el:formGroup>


<el:formGroup style="${hideDetails ? "display: none;" : ''}">
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired"
                     controller="contactMethod" action="autocomplete" isDisabled="${params.isDisabled ? true : false}"
                     name="contactMethod.id" id="contactMethodId" paramsGenerateFunction="paramsContactMethod"
                     label="${message(code: messagePrefix + '.contactMethod.label', default: 'contactMethod')}"
                     values="${[[contactInfo?.contactMethod?.id, contactInfo?.contactMethod?.descriptionInfo?.localName]]}"/>
</el:formGroup>


%{--<el:formGroup>--}%
%{--<el:dateField name="fromDate"  size="8" class=" " label="${message(code:messagePrefix+'.fromDate.label',default:'fromDate')}" value="${contactInfo?.fromDate}" />--}%
%{--</el:formGroup>--}%


%{--<el:formGroup>--}%
%{--<el:dateField name="toDate"  size="8" label="${message(code:messagePrefix+'.toDate.label',default:'toDate')}" value="${contactInfo?.toDate}" />--}%
%{--</el:formGroup>--}%
<div id="contactInfoLocationWrapperId">
    <lay:wall title="${g.message(code: messagePrefix + '.address.label',default:'address')}" id="addressWall">
        <g:render template="/pcore/location/wrapper" model="[
                showCountryWithOutRequired: true,
                hiddenDetails    : true,
                size             : 8,
                location         : contactInfo?.address
        ]"/>
    </lay:wall>
</div>

<el:formGroup class="valueFormGroup">
    <el:textArea name="value" size="8" class=" "
                 label="${hideDetails ? message(code: 'bordersSecurityCoordination.addressDetails.label', default: 'value') : message(code: messagePrefix + '.value.label', default: 'value')}"
                 value="${contactInfo?.value}"/>
</el:formGroup>

<script>
    var addressContactMethodsArray = [
        '${ps.police.pcore.enums.v1.ContactMethod.CURRENT_ADDRESS.value()}',
        '${ps.police.pcore.enums.v1.ContactMethod.ORIGINAL_ADDRESS.value()}',
        '${ps.police.pcore.enums.v1.ContactMethod.WORK_ADDRESS.value()}',
        '${ps.police.pcore.enums.v1.ContactMethod.OTHER_ADDRESS.value()}'];

    function paramsContactMethod() {
        var justAddress = "${params["justAddress"]}";
        if (justAddress == "true" || justAddress == "false") {
            if (justAddress == "true") {
                return {
                    "ids[]": "${[
                ps.police.pcore.enums.v1.ContactMethod.CURRENT_ADDRESS.value(),
                ps.police.pcore.enums.v1.ContactMethod.ORIGINAL_ADDRESS.value(),
                ps.police.pcore.enums.v1.ContactMethod.WORK_ADDRESS.value(),
                ps.police.pcore.enums.v1.ContactMethod.OTHER_ADDRESS.value()
                ]}"
                }
            } else if (justAddress == "false") {
                return {
                    "ids[]": "${[
                ps.police.pcore.enums.v1.ContactMethod.FAX.value(),
                ps.police.pcore.enums.v1.ContactMethod.MOBILE_NUMBER.value(),
                ps.police.pcore.enums.v1.ContactMethod.PHONE_NUMBER.value(),
                ps.police.pcore.enums.v1.ContactMethod.INTERNAL_EXTENSION.value(),
                ps.police.pcore.enums.v1.ContactMethod.CALL_SIGN.value(),
                ps.police.pcore.enums.v1.ContactMethod.EMAIL.value(),
                ps.police.pcore.enums.v1.ContactMethod.WEBSITE.value(),
                ps.police.pcore.enums.v1.ContactMethod.PAGER.value(),
                ]}"
                }
            } else {
                return {}
            }
        } else {
            return {}
        }

    }

    $("#contactMethodId").change(function () {

        var selectedContactMethod = $(this).val();
        var selectedContactMethodText = "";
        if ($('#contactMethodId').val() && $('#contactMethodId').data("select2") && $('#contactMethodId').select2('data').length > 0) {
            selectedContactMethodText = $('#contactMethodId').select2('data')[0].text;
        }
        if ($.inArray(selectedContactMethod, addressContactMethodsArray) >= -1) {
            $("#contactInfoLocationWrapperId").show();
            $(".valueFormGroup div").removeClass("isRequired");
            if (${withPhoneNumber!=true || !withPhoneNumber}) {
                $(".valueFormGroup label").text('${message(code: 'default.details.label')} ' + selectedContactMethodText);
                $("#value").removeClass("isRequired");
            }


        } else {
            $("#contactInfoLocationWrapperId").hide();
            $(".valueFormGroup div").addClass("isRequired");
            if (${withPhoneNumber!=true || !withPhoneNumber}) {
                $(".valueFormGroup label").text('${messagePrefix=="contactInfoAddress"?message(code: 'default.details.label'):message(code: 'default.value.label')}  ' + selectedContactMethodText);
                $("#value").addClass("isRequired");
            }
        }

    });

    $("#relatedObjectType").change(function () {
        if ($(this).val() == '${ps.police.pcore.enums.v1.RelatedParty.ORGANIZATION}') {
            gui.formValidatable.addRequiredField('contactInfoForm', "${organizationCallBackId?:'organizationId'}");
            gui.formValidatable.removeRequiredField('contactInfoForm', "${personCallBackId?:'personId'}");
            $("#organizationFormGroup").show();
            $("#personFormGroup").hide();
        } else if ($(this).val() == '${ps.police.pcore.enums.v1.RelatedParty.PERSON}') {
            gui.formValidatable.removeRequiredField('contactInfoForm', "${organizationCallBackId?:'organizationId'}");
            gui.formValidatable.addRequiredField('contactInfoForm', "${personCallBackId?:'personId'}");
            $("#organizationFormGroup").hide();
            $("#personFormGroup").show();
        } else {
            gui.formValidatable.removeRequiredField('contactInfoForm', "${organizationCallBackId?:'organizationId'}");
            gui.formValidatable.removeRequiredField('contactInfoForm', "${personCallBackId?:'personId'}");
            $("#organizationFormGroup").hide();
            $("#personFormGroup").hide();
        }
    });

    $(document).ready(function () {
        $("#relatedObjectType").trigger("change");
        $("#contactMethodId").trigger("change");
    });
</script>
