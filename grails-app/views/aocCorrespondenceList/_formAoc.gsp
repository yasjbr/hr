<%@ page import="ps.gov.epsilon.aoc.enums.v1.EnumCorrespondencePartyType; ps.gov.epsilon.aoc.correspondences.AocCorrespondenceListParty; ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceDirection; ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceStatus" %>
<el:hiddenField name="correspondenceDirection" value="${aocCorrespondenceList?.correspondenceDirection}"/>
<g:hiddenField name="parentCorrespondenceList.id" value="${aocCorrespondenceList?.parentCorrespondenceList?.id}"/>
<el:formGroup>
    <g:if test="${aocCorrespondenceList?.id || aocCorrespondenceList?.correspondenceType}">
        <el:textField name="correspondenceTypeText" size="6" isReadOnly="true"
                      label="${message(code: 'aocCorrespondenceList.correspondenceType.label', default: 'correspondenceType')}"
                      value="${message(code: 'EnumCorrespondenceType.' + aocCorrespondenceList?.correspondenceType)}"/>
        <el:hiddenField name="correspondenceType" value="${aocCorrespondenceList?.correspondenceType}"/>
    </g:if>
    <g:else>
        <el:select valueMessagePrefix="EnumCorrespondenceType"
                   from="${ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceType.getPermetedValues()}" name="correspondenceType"
                   size="6"
                   class=" isRequired"
                   label="${message(code: 'aocCorrespondenceList.correspondenceType.label', default: 'correspondenceType')}"
                   value="${aocCorrespondenceList?.correspondenceType}"/>

    </g:else>
</el:formGroup>
<g:if test="${aocCorrespondenceList?.correspondenceDirection == EnumCorrespondenceDirection.INCOMING}">
    <g:render template="/aocCorrespondenceListParty/autoCompleteWrapper"
              model="[isClassReadOnly: aocCorrespondenceList?.id != null,
                      isReadOnly     : aocCorrespondenceList?.id != null,
                      partyClass     : 'isRequired',
                      centralizedWithAOC: 'false',
                      typeSequence   : 1,
                      party          : aocCorrespondenceList?.sendingParty]"/>
</g:if>
<g:else>
    <g:render template="/aocCorrespondenceListParty/autoCompleteWrapper"
              model="[isClassReadOnly: aocCorrespondenceList?.id != null,
                      isReadOnly     : aocCorrespondenceList?.id != null,
                      partyClass     : 'isRequired',
                      typeSequence   : 1,
                      centralizedWithAOC: '',
                      party          : aocCorrespondenceList?.receivingParty]"/>
</g:else>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="6" controller="province"
                     action="autocomplete" name="province.id" id="provinceId"
                     label="${message(code: 'aocCorrespondenceList.province.label')}" onchange="resetProvinceLocation();"
                     values="${[[aocCorrespondenceList?.province?.id, aocCorrespondenceList?.province?.toString()]]}"/>
    <el:autocomplete optionKey="id" optionValue="name" size="6" controller="provinceLocation" action="autocomplete"
                     name="provinceLocation.id" id="provinceLocationId" paramsGenerateFunction="provinceParams"
                     label="${message(code: 'aocCorrespondenceList.provinceLocation.label')}"
                     values="${[[aocCorrespondenceList?.provinceLocation?.id, aocCorrespondenceList?.provinceLocation?.toString()]]}"/>
</el:formGroup>

<g:render template="serialNumberForm" model="[aocCorrespondenceList: aocCorrespondenceList, isReadOnly: false,
                                              serialClass          : 'isRequired', prefix: '']"/>

<g:if test="${aocCorrespondenceList?.parentCorrespondenceList}">
    <g:render template="serialNumberForm" model="[aocCorrespondenceList: aocCorrespondenceList.parentCorrespondenceList,
                                                  isReadOnly           : true, prefix: 'parent', serialClass: '']"/>
</g:if>

<el:formGroup>
    <g:hiddenField name="currentStatus" value="${EnumCorrespondenceStatus.CREATED}" />
    %{--<el:select valueMessagePrefix="EnumCorrespondenceStatus" class=" isRequired" name="currentStatus" size="6"--}%
               %{--from="[EnumCorrespondenceStatus.CREATED]"--}%
               %{--value="${aocCorrespondenceList?.currentStatus}"--}%
               %{--label="${message(code: 'aocCorrespondenceList.currentStatus.label', default: 'currentStatus')}"/>--}%
    <el:textField name="receivedBy" size="6" class="" value="${aocCorrespondenceList?.receivedBy}"
                  label="${message(code: 'aocCorrespondenceList.receivedBy.label', default: 'receivedBy')}"/>
    <el:textField name="deliveredBy" size="6" class=""
                  label="${message(code: 'aocCorrespondenceList.deliveredBy.label', default: 'deliveredBy')}"
                  value="${aocCorrespondenceList?.deliveredBy}"/>
</el:formGroup>
<el:formGroup>
    <el:dateField name="deliveryDate" size="6" class=" isRequired" isMaxDate="true"
                  label="${message(code: 'aocCorrespondenceList.deliveryDate.label', default: 'deliveryDate')}"
                  value="${aocCorrespondenceList?.deliveryDate}"/>
    <g:if test="${aocCorrespondenceList?.correspondenceDirection == EnumCorrespondenceDirection.INCOMING &&
            aocCorrespondenceList.parentCorrespondenceList == null}">
        <el:textField name="originalSerialNumber" size="6" class=" isRequired"
                      label="${message(code: 'hrCorrespondence.serialNumber.label', default: 'serialNumber')}"
                      value="${aocCorrespondenceList?.originalSerialNumber}"/>
    </g:if>
</el:formGroup>
<el:formGroup>
    <el:textField name="name" size="6" class=" isRequired"
                  label="${message(code: 'aocCorrespondenceList.name.label', default: 'name')}"
                  value="${aocCorrespondenceList?.name}"/>
</el:formGroup>
<el:formGroup>
    <el:textAreaDescription name="coverLetter" id="coverLetter" size="8" class=" " labelSize="3"
                            label="${message(code: 'vacationList.coverLetter.label', default: 'coverLetter')}"
                            value="${aocCorrespondenceList?.coverLetter}"/>

    <el:modalLink preventCloseOutSide="true" class="btn btn-sm btn-round width-135"
                  link="${createLink(controller: 'correspondenceTemplate', action: 'listModal')}" label="">
        <i class="fa fa-hand-o-up"></i>
        <g:message code="default.button.select.label"/>
    </el:modalLink>
</el:formGroup>
<el:formGroup>
    <el:textArea name="notes" size="8" class=" " labelSize="3" rows="3"
                 label="${message(code: 'aocCorrespondenceList.notes.label', default: 'Notes')}"
                 value="${aocCorrespondenceList?.notes}"/>
</el:formGroup>

<g:if test="${aocCorrespondenceList?.correspondenceDirection == EnumCorrespondenceDirection.OUTGOING}">
    <g:render template="copyToPartyList"
              model="[copyToPartyList: aocCorrespondenceList?.copyToPartyList, showActions: true]"/>
</g:if>
