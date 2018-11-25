<el:formGroup>
    <el:textField name="listNamePrefix" size="8" class=""
                  label="${message(code: 'disciplinaryListJudgmentSetup.listNamePrefix.label', default: 'listNamePrefix')}"
                  value="${disciplinaryListJudgmentSetup?.listNamePrefix}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="disciplinaryCategory"
                     action="autocomplete" name="disciplinaryCategory.id"
                     label="${message(code: 'disciplinaryListJudgmentSetup.disciplinaryCategory.label', default: 'disciplinaryCategory')}"
                     values="${[[disciplinaryListJudgmentSetup?.disciplinaryCategory?.id, disciplinaryListJudgmentSetup?.disciplinaryCategory?.descriptionInfo?.localName]]}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="disciplinaryJudgment"
                     action="autocomplete" name="disciplinaryJudgment.id"
                     label="${message(code: 'disciplinaryListJudgmentSetup.disciplinaryJudgment.label', default: 'disciplinaryJudgment')}"
                     values="${[[disciplinaryListJudgmentSetup?.disciplinaryJudgment?.id, disciplinaryListJudgmentSetup?.disciplinaryJudgment?.descriptionInfo?.localName]]}"/>
</el:formGroup>

