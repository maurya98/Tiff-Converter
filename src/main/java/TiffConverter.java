import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Timer;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import net.miginfocom.swing.MigLayout;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

/*
 * Author :  Saurabh Kumar Maurya
 * Date : 18/07/2021 
 */
public class TiffConverter extends JFrame {

	private static final long serialVersionUID = 1L;
	static File file;
	private JPanel panel = new JPanel();
	private JPanel contentPane;
	JLabel lblNewLabel = new JLabel("Select PDF");
	JLabel lblNewLabel_1 = new JLabel("Select Output Folder");
	JLabel lblDpi = new JLabel("DPI");
	JLabel lblImageType = new JLabel("Image Type");
	JLabel lblCompressionType = new JLabel("Compression Type");
	private JButton btnConvetToTiff = new JButton("Convert");
	private JButton btnOutputFolder = new JButton("Browse");
	private JButton btnSelectFile = new JButton("Browse");
	private JTextField filePath = new JTextField();
	private JTextField outputFolder = new JTextField();
	private JComboBox<String> dpiCombo = new JComboBox<String>();
	private JComboBox<String> imageTypeCombo = new JComboBox<String>();
	private JComboBox<String> compressionCombo = new JComboBox<String>();
	final JProgressBar progressBar = new JProgressBar(JProgressBar.HORIZONTAL);
	static PDDocument document;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					TiffConverter frame = new TiffConverter();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public TiffConverter() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(TiffConverter.class.getResource("/com/icon/file-icon-tiff.png")));
		// JOptionPane.showMessageDialog(contentPane,
		// System.getProperty("user.dir")+"\\src\\main\\resources\\tiff-file-18-504403.png");
		setResizable(false);
		setTitle("File Converter");
		initComponent();
	}

	/**
	 * Create the frame.
	 */
	public void initComponent() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 536, 208);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		panel.setBackground(Color.WHITE);
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new MigLayout("", "[109px][4px][150px][10px][70px][2px][64px][4px][85px]",
				"[28px][27px][27px][25px][25px]"));

		lblNewLabel.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		panel.add(lblNewLabel, "cell 0 0,grow");

		filePath.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		panel.add(filePath, "cell 2 0 5 1,growx,aligny top");
		filePath.setColumns(10);
		btnSelectFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnSelectFileActionPerformed(e);
			}
		});
		btnSelectFile.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		panel.add(btnSelectFile, "cell 8 0,growx,aligny bottom");

		btnConvetToTiff.setEnabled(false);
		btnConvetToTiff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnConvertToTiffActionPerformed(e);
			}
		});

		lblNewLabel_1.setHorizontalAlignment(SwingConstants.LEFT);
		lblNewLabel_1.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		panel.add(lblNewLabel_1, "cell 0 1,alignx left,aligny center");

		outputFolder.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		outputFolder.setToolTipText("Select the folder and also write the file name with extension");
		panel.add(outputFolder, "cell 2 1 5 1,grow");
		outputFolder.setColumns(10);
		btnConvetToTiff.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		panel.add(btnConvetToTiff, "cell 8 4,growx,aligny top");
		btnOutputFolder.setEnabled(false);
		btnOutputFolder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnOutputFolderActionPerformed(e);
			}
		});
		btnOutputFolder.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		panel.add(btnOutputFolder, "cell 8 1,growx,aligny bottom");

		lblDpi.setHorizontalTextPosition(SwingConstants.LEFT);
		lblDpi.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		panel.add(lblDpi, "cell 0 2,grow");

		dpiCombo.setModel(new DefaultComboBoxModel(new String[] {"96", "100", "150", "200", "250", "300"}));
		dpiCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		panel.add(dpiCombo, "cell 2 2,growx,aligny top");

		lblCompressionType.setHorizontalTextPosition(SwingConstants.LEFT);
		lblCompressionType.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		panel.add(lblCompressionType, "cell 0 3,alignx left,growy");
		compressionCombo.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				compressionComboItemStateChanged(e);
			}
		});

		compressionCombo.setModel(new DefaultComboBoxModel<String>(new String[] { "Deflate", "JPEG", "LZW", "PackBits", "ZLib", "CCITT T.4", "CCITT T.6" }));
		compressionCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		panel.add(compressionCombo, "cell 2 3,grow");

		lblImageType.setHorizontalTextPosition(SwingConstants.LEFT);
		lblImageType.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		panel.add(lblImageType, "cell 4 2,grow");

		imageTypeCombo.setModel(new DefaultComboBoxModel<String>(new String[] { "BINARY", "ARGB", "RGB", "GRAY" }));
		imageTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		panel.add(imageTypeCombo, "cell 6 2 3 1,growx,aligny bottom");

		panel.add(progressBar, "cell 0 4 7 1,growx,aligny bottom");

	}

	private void btnConvertToTiffActionPerformed(ActionEvent e) {
		if (filePath.getText() == "" || outputFolder.getText() == "") {
			JOptionPane.showMessageDialog(contentPane, "Please fill the empty fields");
		} else {
			btnConvetToTiff.setEnabled(false);
			btnOutputFolder.setEnabled(false);
			Thread th = new Thread(new Runnable() {
				public void run() {
					try {
						progressBar.setIndeterminate(true);
						convertToTiff();
					} catch (Exception e) {
						JOptionPane.showMessageDialog(contentPane, e.getMessage());
						//e.printStackTrace();
					}
				}
			});
			th.start();
		}
	}

//Event Handling
	public void btnOutputFolderActionPerformed(ActionEvent e) {
		JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnVal = jfc.showDialog(null, "Open");
		String outfilename = file.getName().substring(0, file.getName().length() - 4);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			outputFolder.setText(jfc.getSelectedFile().getAbsolutePath() + "\\" + outfilename + ".tiff");
			btnConvetToTiff.setEnabled(true);
		}
	}

	public void btnSelectFileActionPerformed(ActionEvent e) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("PDF", "pdf"));
		fileChooser.setAcceptAllFileFilterUsed(false);
		int returnVal = fileChooser.showDialog(this, "Open");
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fileChooser.getSelectedFile();
			filePath.setText(file.getAbsolutePath());
			btnOutputFolder.setEnabled(true);
		}
	}

	public void compressionComboItemStateChanged(ItemEvent e) {
		if (compressionCombo.getSelectedItem().equals("CCITT T.6")
				|| compressionCombo.getSelectedItem().equals("CCITT T.4")) {
			imageTypeCombo.setSelectedItem("BINARY");
			imageTypeCombo.setEnabled(false);
		} else {
			imageTypeCombo.setEnabled(true);
		}
	}

	public void convertToTiff() {
		final long startTime = System.currentTimeMillis();
		try {
			document = PDDocument.load(file);
			// save page caputres to file.
			File file = new File(outputFolder.getText());
			ImageOutputStream ios = ImageIO.createImageOutputStream(file);
			ImageWriter writer = ImageIO.getImageWritersByFormatName("tiff").next();
			writer.setOutput(ios);
			PDFRenderer pdfRenderer = new PDFRenderer(document);
			ImageType imageType = ImageType.RGB;
			int pageCount = document.getNumberOfPages();

			BufferedImage[] images = new BufferedImage[pageCount];
			writer.setOutput(ios);
			ImageWriteParam params = writer.getDefaultWriteParam();
			params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);

			// Compression: None, PackBits, ZLib, Deflate, LZW, JPEG and CCITT
			// variants allowed
			params.setCompressionType(compressionCombo.getSelectedItem().toString());

			if (imageTypeCombo.getSelectedItem() == "ARGB") {
				imageType = ImageType.ARGB;
			} else if (imageTypeCombo.getSelectedItem() == "BINARY") {
				imageType = ImageType.BINARY;
			} else if (imageTypeCombo.getSelectedItem() == "GRAY") {
				imageType = ImageType.GRAY;
			}
			writer.prepareWriteSequence(null);
			// System.out.println(document.getNumberOfPages());

			for (int page = 0; page < document.getNumberOfPages(); page++) {
				BufferedImage image = pdfRenderer.renderImageWithDPI(page,
						Integer.parseInt(dpiCombo.getSelectedItem().toString()), imageType);
				images[page] = image;
				IIOMetadata metadata = writer.getDefaultImageMetadata(new ImageTypeSpecifier(image), params);
				writer.writeToSequence(new IIOImage(image, null, metadata), params);

			}
			progressBar.setIndeterminate(false);
			progressBar.setValue(100);
			// clean up resources
			ios.flush();
			ios.close();
			writer.dispose();
			document.close();
			btnConvetToTiff.setEnabled(true);
			btnOutputFolder.setEnabled(true);
			final long endTime = System.currentTimeMillis();
			System.out.println("Total execution time: " + (endTime - startTime));
		} catch (Exception ex) {
			progressBar.setIndeterminate(false);
			progressBar.setValue(0);
			JOptionPane.showMessageDialog(contentPane, ex.getMessage()+"\n"+ ex.getCause());
		}
	}
}
