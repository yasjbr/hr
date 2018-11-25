<%@ page import="ps.gov.epsilon.aoc.enums.v1.EnumCorrespondencePartyClass" %>
<% def size= columnSize?:6 %>
<el:formGroup>
    <g:hiddenField name="partyType" id="partyType" value="${party?.partyType}"/>
    <g:if test="${isClassReadOnly || isReadOnly}">
        <g:hiddenField name="${party?.partyType?.toString()}.partyClass" value="${party?.partyClass}"/>

        <el:textField name="${party?.partyType?.toString()}ClassText" isDisabled="true"
                      label="${message(code: 'aocCorrespondenceList.'+party?.partyType?.toString()+'.class.label')}"
                      size="${size}" value="${message(code: 'EnumCorrespondencePartyClass.' + party?.partyClass?.toString())}"/>
    </g:if>
    <g:else>
        <el:select valueMessagePrefix="EnumCorrespondencePartyClass"
                   from="${EnumCorrespondencePartyClass.values()}" name="${party?.partyType?.toString()}.partyClass"
                   size="${size}" class="${partyClass} partyClassSelect" id="${party?.partyType?.toString()}_partyClassSelect"
                   label="${message(code: 'aocCorrespondenceList.'+party?.partyType?.toString()+'.class.label')}"
                   value="${party?.partyClass}"/>
    </g:else>
    <g:if test="${isReadOnly}">
        <g:hiddenField name="${party?.partyType?.toString()}.${party?.partyClass?.toString()}Id" value="${party?.partyId}"/>
        <el:textField name="${party?.partyType?.toString()}IdText" isDisabled="true"
                      label="${message(code: 'aocCorrespondenceList.'+party?.partyType?.toString()+'.name.label')}"
                      size="${size}" value="${party?.name}"/>
    </g:if>
    <g:else>
        <div class="${party?.partyType?.toString()+'Div class'+party?.partyType?.toString()+EnumCorrespondencePartyClass.FIRM.toString()}" style="display: none">
            <el:autocomplete optionKey="id" optionValue="name" size="${size}" class="${partyClass}" controller="firm"
                             action="autocomplete" name="${party?.partyType?.toString()+'.'+EnumCorrespondencePartyClass.FIRM.toString()}Id"
                             id="${party?.partyType?.toString()+'_'+EnumCorrespondencePartyClass.FIRM.toString()}Id"
                             label="${message(code: 'aocCorrespondenceList.'+party?.partyType?.toString()+'.name.label')}"
                             paramsGenerateFunction="firmParams" values="${[[party?.partyId, party?.name]]}"/>
        </div>
        <div class="${party?.partyType?.toString()+'Div class'+party?.partyType?.toString()+EnumCorrespondencePartyClass.COMMITTEE.toString()}" style="display: none">
            <el:autocomplete optionKey="id" optionValue="name" size="${size}" class="${partyClass}" controller="committee"
                             action="autocomplete" name="${party?.partyType?.toString()+'.'+EnumCorrespondencePartyClass.COMMITTEE.toString()}Id"
                             id="${party?.partyType?.toString()+'_'+EnumCorrespondencePartyClass.COMMITTEE.toString()}Id"
                             label="${message(code: 'aocCorrespondenceList.'+party?.partyType?.toString()+'.name.label')}"
                             values="${[[party?.partyId, party?.name]]}"/>
        </div>
        <div class="${party?.partyType?.toString()+'Div class'+party?.partyType?.toString()+EnumCorrespondencePartyClass.ORGANIZATION.toString()}" style="display: none">
            <el:autocomplete optionKey="id" optionValue="name" size="${size}" class="${partyClass}" controller="pcore"
                             action="organizationAutoComplete" name="${party?.partyType?.toString()+'.'+EnumCorrespondencePartyClass.ORGANIZATION.toString()}Id"
                             label="${message(code: 'aocCorrespondenceList.'+party?.partyType?.toString()+'.name.label')}"
                             values="${[[party?.partyId, party?.name]]}"
                             id="${party?.partyType?.toString()+'_'+EnumCorrespondencePartyClass.ORGANIZATION.toString()}Id"/>
        </div>
    </g:else>
</el:formGroup>

<script>
    $(document).ready(function(){
        var type= $("#partyType").val();
        if("${party?.partyClass?.toString()}" != ""){
            showHideDiv("${party?.partyClass?.toString()}", $("#partyType").closest('.form-group'));
        }
        $('.partyClassSelect').change(function () {
            console.log("select called with value " + $(this).val());
            showHideDiv($(this).val(), $(this).closest('.form-group'));
        });
    });

    function showHideDiv(partyClass, parentDiv){
        var type= parentDiv.find("#partyType").val();
        console.log("type = " + type);
        $('.'+type+'Div').hide(1);
        $('.class'+type+partyClass).show(1);
    }

    /**
     * to get only firms not centralized with AOC
     */
    function firmParams() {
        var searchParams = {};
        if("${centralizedWithAOC}" != ""){
            searchParams.centralizedWithAOC = "${centralizedWithAOC}";
        }
        return searchParams;
    }

</script>