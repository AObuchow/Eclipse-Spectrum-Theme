package com.aobuchow.themes.spectrum.preferences;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Control to be displayed on the preference page.
 * <p>
 * <ul>
 * <li>Has scales for the three settings; hue, saturation and brightness</li>
 * <li>Has a 4 radio buttons to select which theme color to affect; all, accent, base or background</li>
 * <li>Has a CSS scheme text widget</li>
 * <li>Has links to open the Gitub repo page and the Github issues page</li>
 * <ul>
 * </p>
 */
public class SpectrumPreferencesControl extends Composite {

	private Scale hueScale;

	private Scale saturationScale;

	private Scale brightnessScale;
	
	private Button allColorsButton;
	
	private Button accentColorButton;
	
	private Button baseColorButton;
	
	private Button backgroundColorButton;

	private StyledText cssText;

	private Link repoLink;

	private Link issuesLink;

	private FormToolkit kit;

	public SpectrumPreferencesControl(Composite parent, int style) {
		super(parent, style);

		kit = new FormToolkit(parent.getDisplay());

		create();

		addDisposeListener(e -> {
			if (kit != null) {
				kit.dispose();
			}
		});
	}

	private void create() {
		setLayout(GridLayoutFactory.fillDefaults().create());
		createSettingsGroup();
		createComunityGroup();
	}

	private void createSettingsGroup() {
		Group group = new Group(this, SWT.SHADOW_ETCHED_IN);
		group.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		group.setText(Messages.SpectrumPreferencePage_CustomizationGroup);
		group.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
		
		createSelectedColorButtons(group);

		hueScale = createScaleLine(group, Messages.PreferencesPage_LabelHue, 0, 360);
		saturationScale = createScaleLine(group, Messages.PreferencesPage_LabelSaturation, 0, 100);
		brightnessScale = createScaleLine(group, Messages.PreferencesPage_LabelLuminance, 0, 100);

		createCssExandable(group);
	}

	private void createSelectedColorButtons(Group group) {
		allColorsButton = new Button(group, SWT.RADIO);
		allColorsButton.setText("Global Colors");
		allColorsButton.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		accentColorButton = new Button(group, SWT.RADIO);
		accentColorButton.setText("Accent Color");
		accentColorButton.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		baseColorButton = new Button(group, SWT.RADIO);
		baseColorButton.setText("Base Color");
		baseColorButton.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		backgroundColorButton = new Button(group, SWT.RADIO);
		backgroundColorButton.setText("Background Color");
		backgroundColorButton.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
	}

	private Scale createScaleLine(Composite parent, String labelText, int min, int max) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(labelText);
		label.setLayoutData(GridDataFactory.swtDefaults().align(SWT.BEGINNING, SWT.CENTER).create());

		Scale scale = new Scale(parent, SWT.HORIZONTAL);
		scale.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		scale.setMinimum(min);
		scale.setMaximum(max);
		scale.setIncrement(5);
		scale.setPageIncrement(5);

		return scale;
	}

	private void createCssExandable(Composite parent) {
		ExpandableComposite expandableComposite = kit.createExpandableComposite(parent, ExpandableComposite.TWISTIE);
		expandableComposite.setLayoutData(GridDataFactory.fillDefaults().span(2, 1).grab(true, true).create());
		expandableComposite.setText("CSS");

		cssText = new StyledText(expandableComposite, SWT.BORDER);
		expandableComposite.setClient(cssText);
	}

	private void createComunityGroup() {
		Group group = new Group(this, SWT.SHADOW_ETCHED_IN);
		group.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		group.setText(Messages.SpectrumPreferencePage_CommunityGroup);
		group.setLayout(GridLayoutFactory.swtDefaults().create());

		repoLink = createLink(group, Messages.SpectrumPreferencePage_RepositoryLink,
				Messages.SpectrumPreferencePage_GithubURL);

		issuesLink = createLink(group, Messages.SpectrumPreferencePage_BugReportLink,
				Messages.SpectrumPreferencePage_GithubURL_Issues);
	}

	private Link createLink(Composite parent, String text, String targetUrl) {
		Link link = new Link(parent, SWT.NONE);
		link.setText(text);
		return link;
	}

	public Scale getHueScale() {
		return hueScale;
	}

	public Scale getSaturationScale() {
		return saturationScale;
	}

	public Scale getBrightnessScale() {
		return brightnessScale;
	}

	public StyledText getCssText() {
		return cssText;
	}

	public Link getRepoLink() {
		return repoLink;
	}

	public Link getIssuesLink() {
		return issuesLink;
	}

	public Button getAllColorsButton() {
		return allColorsButton;
	}

	public Button getAccentColorButton() {
		return accentColorButton;
	}

	public Button getBaseColorButton() {
		return baseColorButton;
	}

	public Button getBackgroundColorButton() {
		return backgroundColorButton;
	}

}
