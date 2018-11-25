<%@ page import="ps.gov.epsilon.aoc.enums.v1.EnumCorrespondencePartyType; ps.gov.epsilon.aoc.correspondences.AocCorrespondenceListParty; ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceStatus" %>

<g:if test="${workflowSearch}">
    <el:formGroup>
        <el:select valueMessagePrefix="EnumCorrespondenceType"
                   from="${ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceType.values()}" name="correspondenceType"
                   size="6" class=""
                   label="${message(code: 'aocCorrespondenceList.correspondenceType.label', default: 'correspondenceType')}"/>
        <el:select valueMessagePrefix="EnumCorrespondenceDirection"
                   from="${ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceDirection.values()}" name="correspondenceDirection"
                   size="6" class=""
                   label="${message(code: 'aocCorrespondenceList.correspondenceDirection.label', default: 'correspondenceDirection')}"/>
    </el:formGroup>
</g:if>
<g:else>
    <g:hiddenField name="correspondenceDirection" value="${correspondenceDirection}" />
    <g:hiddenField name="correspondenceType" value="${correspondenceType}" />
</g:else>


<el:formGroup>
    <el:textField name="code"
                  size="6"
                  class=""
                  label="${message(code: 'aocCorrespondenceList.code.label', default: 'code')}" value=""/>

    <el:textField name="named"
                  size="6"
                  class=""
                  label="${message(code: 'aocCorrespondenceList.name.label', default: 'name')}" value=""/>
</el:formGroup>


<el:formGroup>
   <el:range type="date" size="6" label="${message(code: 'aocCorrespondenceList.deliveryDate.label', default: 'deliveryDate')}" name="deliveryDate"/>
</el:formGroup>


<el:formGroup>


    <el:range type="date" label="${message(code: 'aocCorrespondenceList.archivingDate.label', args: [entity], default: 'archivingDate')}"
              name="archivingDate"
              size="6"/>


    <el:textField name="serialNumber" size="6" class=""
                  label="${message(code: 'aocCorrespondenceList.serialNumber.label',  args: [entity], default: 'serialNumber')}"/>
</el:formGroup>

<el:formGroup>
    <%
        def correspondnenceStatusList
        def correspondnenceStatusValue=null
        if(workflowSearch){
            correspondnenceStatusList= [EnumCorrespondenceStatus.IN_PROGRESS, EnumCorrespondenceStatus.APPROVED,
                                        EnumCorrespondenceStatus.PARTIALLY_APPROVED, EnumCorrespondenceStatus.REJECTED,
                                        EnumCorrespondenceStatus.STOPPED, EnumCorrespondenceStatus.SUBMITTED]
            correspondnenceStatusValue= EnumCorrespondenceStatus.IN_PROGRESS
        }else{
            correspondnenceStatusList= EnumCorrespondenceStatus.values()
        }
    %>

</el:formGroup>
%{--<el:formGroup>--}%
    %{--<el:select valueMessagePrefix="EnumCorrespondenceReceivingParty"--}%
               %{--from="${ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceReceivingParty.values()}" name="receivingParty"--}%
               %{--size="8" class=""--}%
               %{--label="${message(code: 'aocCorrespondenceList.FROM.name.label', default: 'receivingParty')}"/>--}%
%{--</el:formGroup>--}%
%{--<el:formGroup>--}%
    %{--<el:select valueMessagePrefix="EnumCorrespondenceReceivingParty"--}%
               %{--from="${ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceReceivingParty.values()}" name="sendingParty"--}%
               %{--size="8" class=""--}%
               %{--label="${message(code: 'aocCorrespondenceList.TO.name.label', default: 'sendingParty')}"/>--}%
%{--</el:formGroup>--}%

<g:render template="/aocCorrespondenceListParty/autoCompleteWrapper"
          model="[isClassReadOnly: false,
                  isReadOnly     : false,
                  partyClass     : '',
                  centralizedWithAOC: '',
                  typeSequence   : 1,
                  partyTypePrefix: 'from',
                  party          : new AocCorrespondenceListParty(partyType:EnumCorrespondencePartyType.FROM)]"/>

<g:render template="/aocCorrespondenceListParty/autoCompleteWrapper"
          model="[isClassReadOnly: false,
                  isReadOnly     : false,
                  partyClass     : '',
                  centralizedWithAOC: '',
                  typeSequence   : 1,
                  partyTypePrefix: 'to',
                  party          : new AocCorrespondenceListParty(partyType:EnumCorrespondencePartyType.TO)]"/>


<el:formGroup>
    <el:select valueMessagePrefix="EnumCorrespondenceStatus"
               from="${correspondnenceStatusList}" name="currentStatus"
               size="6" class="" value="${correspondnenceStatusValue}"
               label="${message(code: 'aocCorrespondenceList.currentStatus.label', default: 'currentStatus')}"/>
</el:formGroup>
