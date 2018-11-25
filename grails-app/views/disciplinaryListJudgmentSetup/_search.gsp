<el:formGroup>
    <el:textField name="listNamePrefix" size="8" class=""
                  label="${message(code: 'disciplinaryListJudgmentSetup.listNamePrefix.label', default: 'listNamePrefix')}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="disciplinaryCategory"
                     action="autocomplete" name="disciplinaryCategory.id"
                     label="${message(code: 'disciplinaryListJudgmentSetup.disciplinaryCategory.label', default: 'disciplinaryCategory')}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="disciplinaryJudgment"
                     action="autocomplete" name="disciplinaryJudgment.id"
                     label="${message(code: 'disciplinaryListJudgmentSetup.disciplinaryJudgment.label', default: 'disciplinaryJudgment')}"/>
</el:formGroup>

