package edu.uchicago.cs.hao.texdojo.latexeditor.editors.hover;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.AbstractInformationControl;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IInformationControlExtension2;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.util.Geometry;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class LaTeXInformationControl extends AbstractInformationControl
		implements IInformationControlExtension2, ISelectionChangedListener {

	private StyledText fText;

	private ListViewer fList;

	private String input;

	private HoverContext context;

	public LaTeXInformationControl(Shell parent) {
		super(parent, true);
		create();
	}

	@Override
	public boolean hasContents() {
		return input != null;
	}

	@Override
	protected void createContent(Composite parent) {
		parent.setLayout(new GridLayout());

		fText = new StyledText(parent, SWT.MULTI | SWT.READ_ONLY);
		fText.setForeground(parent.getForeground());
		fText.setBackground(parent.getBackground());
		fText.setFont(JFaceResources.getDialogFont());
		fText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		fList = new ListViewer(parent, SWT.SINGLE);
		fList.setContentProvider(new IStructuredContentProvider() {
			@Override
			public Object[] getElements(Object inputElement) {
				return ((List<String>) inputElement).toArray();
			}
		});
		fList.addSelectionChangedListener(this);
		fList.getList().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	@Override
	public void setInput(Object input) {
		this.context = null;
		if (input instanceof HoverContext) {
			this.context = (HoverContext) input;
			setInformation(context.information);
		} else if (input instanceof String) {
			setInformation((String) input);
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void setInformation(String information) {
		// Parse input
		this.input = information;
		if (input == null)
			return;
		if (input.startsWith("Spell Check")) {
			String[] pieces = input.split("\\n");
			fText.setText(pieces[0]);
			if (pieces.length > 1) {
				// Has suggestions
				List<String> suggestions = Arrays.stream(pieces).skip(1).collect(Collectors.toList());
				fList.setInput(suggestions);
			}
		}
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			getShell().pack(true);
		}

		super.setVisible(visible);
	}

	@Override
	public Rectangle computeTrim() {
		return Geometry.add(super.computeTrim(), fText.computeTrim(0, 0, 0, 0));
	}

	@Override
	public IInformationControlCreator getInformationPresenterControlCreator() {
		return new IInformationControlCreator() {
			@Override
			public IInformationControl createInformationControl(Shell parent) {
				return new LaTeXInformationControl(parent);
			}
		};
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		String selected = ((IStructuredSelection) event.getSelection()).getFirstElement().toString();

		if (this.context != null) {
			IRegion region = this.context.hoverRegion;
			try {
				this.context.document.replace(region.getOffset(), region.getLength(), selected);
			} catch (BadLocationException e) {

			}
		}

		this.dispose();
	}

}
