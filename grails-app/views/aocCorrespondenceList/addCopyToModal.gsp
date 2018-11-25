<%@ page import="ps.gov.epsilon.aoc.enums.v1.EnumCorrespondencePartyType; ps.gov.epsilon.aoc.correspondences.AocCorrespondenceListParty" %>
<el:validatableModalForm title="${message(code: 'aocCorrespondenceList.copyToParty.add.btn')}"
                         width="70%"
                         name="sendDataForm"
                         controller="aocCorrespondenceList"
                         hideCancel="true"
                         hideClose="true"
                         action="save">
    <msg:modal/>

    <g:render template="/aocCorrespondenceListParty/autoCompleteWrapper"
              model="[isClassReadOnly: false,
                      isReadOnly: false,
                      partyClass: 'isRequired',
                      typeSequence: 2,
                      centralizedWithAOC: '',
                      party     : new ps.gov.epsilon.aoc.correspondences.AocCorrespondenceListParty(partyType: ps.gov.epsilon.aoc.enums.v1.EnumCorrespondencePartyType.COPY)]" />

    <el:row/>

    <el:formButton functionName="addButton"
                   onClick="addCopyToParty()" />

    <el:formButton functionName="close"
                   onClick="closeCopyToModal()" />

</el:validatableModalForm>