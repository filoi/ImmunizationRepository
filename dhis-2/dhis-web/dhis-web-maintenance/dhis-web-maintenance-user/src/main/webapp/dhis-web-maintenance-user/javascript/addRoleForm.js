jQuery( document ).ready( function()
{
	jQuery( "#name" ).focus();

	validation2( 'addRoleForm', function( form )
	{
		selectAllById( 'selectedList' );
		selectAllById( 'selectedListAuthority' );
		selectAllById( 'selectedListDataElement' );
		form.submit();
	}, {
		'rules' : getValidationRules("role")
	} );

	/* remote validation */
	checkValueIsExist( "name", "validateRole.action" );

	sortList( 'availableListAuthority', 'ASC' );
} );
