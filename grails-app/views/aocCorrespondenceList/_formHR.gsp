<g:hiddenField name="hrCorrespondenceList.receivingParty"
               value="${ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.SARAYA}"/>
<el:formGroup>
    <el:textField name="hrCorrespondenceList.name" size="6" class=" isRequired"
                  label="${message(code: 'vacationList.name.label', default: 'name')}"
                  value="${aocCorrespondenceList?.hrCorrespondenceList?.name}"/>
    <g:if test="${aocCorrespondenceList?.correspondenceDirection == ps.gov.epsilon.aoc.enums.v1.EnumCorrespondenceDirection.OUTGOING}">
        <el:textField name="hrCorrespondenceList.manualIncomeNo" size="6" class=""
                      label="${message(code: 'maritalStatusList.manualIncomeNo.label', default: 'serialNumber')}"
                      value="${aocCorrespondenceList?.hrCorrespondenceList?.manualIncomeNo}"/>
    </g:if>
    <g:else>
        <el:textField name="hrCorrespondenceList.manualOutgoingNo" size="6" class=" isRequired"
                      label="${message(code: 'maritalStatusList.manualOutgoingNo.label', default: 'serialNumber')}"
                      value="${aocCorrespondenceList?.hrCorrespondenceList?.manualOutgoingNo}"/>
    </g:else>
</el:formGroup>

<el:formGroup>
    <el:textAreaDescription name="hrCorrespondenceList.coverLetter" id="coverLetter" size="8" class=" " labelSize="3"
                            label="${message(code: 'vacationList.coverLetter.label', default: 'coverLetter')}"
                            value="${aocCorrespondenceList?.hrCorrespondenceList?.coverLetter}"/>

    <el:modalLink preventCloseOutSide="true" class="btn btn-sm btn-round width-135"
                  link="${createLink(controller: 'correspondenceTemplate', action: 'listModal')}" label="">
        <i class="fa fa-hand-o-up"></i>
        <g:message code="default.button.select.label"/>
    </el:modalLink>
</el:formGroup>
<el:formGroup>
    <el:textArea name="notes"  size="8" class=" " labelSize="3" rows="3"
                            label="${message(code: 'aocCorrespondenceList.notes.label', default: 'Notes')}"
                            value="${aocCorrespondenceList?.notes}"/>

</el:formGroup>
