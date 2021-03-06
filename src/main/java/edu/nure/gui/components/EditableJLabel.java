package edu.nure.gui.components;

import edu.nure.listener.ValueChangedListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;

/**
 * Create a JPanel with a CardLayout which switches to another JPanel on hover
 * 
 * @author James McMinn
 * 
 */
public class EditableJLabel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JLabel label;
	private JTextField textField;
	private LinkedList<ValueChangedListener> listeners = new LinkedList<ValueChangedListener>();

	/**
	 * Create the new panel
	 * 
	 * @param startText
	 *            The starting text
	 */
	public EditableJLabel(String startText) {
		super();

		// Create the listener and the layout
		CardLayout layout = new CardLayout(0, 0);
		this.setLayout(layout);
		EditableListener hl = new EditableListener();

		// Create the JPanel for the "normal" state
		JPanel labelPanel = new JPanel(new GridLayout(1, 1));
		label = new JLabel(startText);
		labelPanel.add(label);

		// Create the JPanel for the "hover state"
		JPanel inputPanel = new JPanel(new GridLayout(1, 1));
		textField = new JTextField(startText);
		textField.addMouseListener(hl);
		textField.addKeyListener(hl);
		textField.addFocusListener(hl);
		inputPanel.add(textField);

		this.addMouseListener(hl);

		// Set the states
		this.add(labelPanel, "NORMAL");
		this.add(inputPanel, "HOVER");

		// Show the correct panel to begin with
		layout.show(this, "NORMAL");
	}

    public EditableJLabel(String startText, String pattern){
        this(startText);

    }

    /**
     * Get the text from the label
     *
     * @return The text from the label
     */
    public String getText() {
        return this.label.getText();
    }

	/**
	 * Set the text of the component
     *
	 * @param text
	 *            The text to start with
	 */
	public void setText(String text) {
            this.label.setText(text);
            this.textField.setText(text);
	}

    public void clear(){
        listeners.clear();
    }

	/**
	 * Get the text field
	 * 
	 * @return the text field component
	 */
	public JTextField getTextField() {
		return textField;
	}

	/**
	 * Get the label
	 * 
	 * @return the label
	 */
	public JLabel getLabel() {
		return label;
	}

	/**
	 * Set the hover state of the Panel
	 * 
	 * @param hover
	 *            True will set the state to hovering and show the input box.
	 *            False will show the label.
	 */
	public void setHoverState(boolean hover) {
		CardLayout cl = (CardLayout) (this.getLayout());
		
		if (hover)
			cl.show(this, "HOVER");
		else
			cl.show(this, "NORMAL");
	}

	/**
	 * Add a value changed listener to this EditableJLabel
	 * 
	 * @param l
	 */
	public void addValueChangedListener(ValueChangedListener l) {
		this.listeners.add(l);
	}

	/**
	 * Listen for nearly everything happening
	 */
	public class EditableListener implements MouseListener, KeyListener, FocusListener {

		boolean locked = false;
		String oldValue;

		/**
		 * Lock to the text field while we have focus
		 */
		@Override
		public void focusGained(FocusEvent arg0) {
			locked = true;
			oldValue = textField.getText();
            if(oldValue.equals("<unknown>"))
                textField.setText("");
		}

		/**
		 * Release the lock so that we can go back to a JLabel
		 */
		public void release() {
			this.locked = false;
		}

		/**
		 * Check for mouse over
		 */
		@Override
		public void mouseEntered(MouseEvent e) {
			setHoverState(true);
		}

		/**
		 * Check for the mouse exiting and set the sate back to normal if
		 * possible
		 */
		@Override
		public void mouseExited(MouseEvent e) {
			if (!locked)
				setHoverState(false);
		}

		/**
		 * Update the text when focus is lost and release the lock
		 */
		@Override
		public void focusLost(FocusEvent e) {
            String text = textField.getText();
            if(text.isEmpty()) text = "<unknown>";
			setText(text);
			for (ValueChangedListener v : listeners) {
				v.valueChanged(textField.getText(), EditableJLabel.this);
			}
			release();
			mouseExited(null);
		}

		/**
		 * Check for key presses. We're only interested in Enter (save the value
		 * of the field) and Escape (reset the field to its previous value)
		 */
		@Override
		public void keyTyped(KeyEvent e) {
			if (e.getKeyChar() == KeyEvent.VK_ENTER) {
				setText(textField.getText());
				for (ValueChangedListener v : listeners) {
					v.valueChanged(textField.getText(), EditableJLabel.this);
				}
				release();
				mouseExited(null);
			} else if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
				release();
				mouseExited(null);
				setText(oldValue);
			}
		}

		/*
		 * We don't need anything below this point in the Listener Class
		 */
		@Override
		public void mousePressed(MouseEvent e) {
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}

		@Override
		public void mouseClicked(MouseEvent e) {
		}

	}

}

/**
 * A listener for the EditableJLabel. Called when the value of the JLabel is
 * updated.
 * 
 * @author James McMinn
 * 
 */
