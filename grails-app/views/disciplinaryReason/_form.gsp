<g:render template="/DescriptionInfo/wrapper" model="[bean: disciplinaryReason?.descriptionInfo]"/>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="disciplinaryCategory"
                     action="autocomplete" name="disciplinaryCategories.id"
                     label="${message(code: 'disciplinaryReason.disciplinaryCategories.label', default: 'disciplinaryCategories')}"
                     values="${[[disciplinaryReason?.disciplinaryCategories?.id, disciplinaryReason?.disciplinaryCategories?.descriptionInfo?.localName]]}"/>
</el:formGroup>

<el:formGroup>
    <el:dualListBox size="8" optionKey="id" from="${disciplinaryJudgmentList}"
                    values="${disciplinaryReason?.joinedDisciplinaryJudgmentReasons?.disciplinaryJudgment}"
                    label="${message(code: 'disciplinaryReason.joinedDisciplinaryJudgmentReasons.label', default: 'joinedDisciplinaryJudgmentReasons')}"
                    name="judgmentReasons.id"
                    moveOnSelect="false"
                    showFilterInputs="true"
                    isAllowToAdd="true"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="universalCode" size="8" class=""
                  label="${message(code: 'disciplinaryReason.universalCode.label', default: 'universalCode')}"
                  value="${disciplinaryReason?.universalCode}"/>
</el:formGroup>