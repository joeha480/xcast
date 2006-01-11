<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="no"/>

  <!-- *************************************************************** 
   * Kommentar:
   * Testa ordentligt, koden är inte fullt testad, flera buggar kan
   * förekomma. 
   *
   * När ett LI hittas skrivs detta och alla följande LI ut på en gång.
   * Övriga LI ignoreras eftersom de redan skrivits ut.
   **************************************************************** -->

<!-- 
 * Applicera på ett root-element 
 -->
 <!--
<xsl:template match="root">
  <root>
    <xsl:apply-templates/>
  </root>
</xsl:template>
-->


<!-- 
 * Matcha alla LI där det föregående syskonet inte är LI eller om det saknas.
 * Använd fwd-varianten för utskrift.
 -->
<xsl:template match="li[preceding-sibling::*[1][not(self::li)] or not(preceding-sibling::*[1])]">
  <list type="pl">
    <xsl:apply-templates select="." mode="fwd"/>
  </list>
</xsl:template>



<!--
 * Skriv ut detta LI och applicera på närmsta syskon om detta är ett LI.
 * Skriv också ut eventuella attribut.
 -->
<xsl:template match="li" mode="fwd">
  <!--<xsl:copy-of select="."/>-->
  <li>
  	<xsl:for-each select="@*">
  		<xsl:attribute name="{name()}">
  			<xsl:value-of select="."/>
  		</xsl:attribute>
  	</xsl:for-each>
  	<xsl:apply-templates/>
  </li>
  <xsl:apply-templates select="following-sibling::*[1][self::li]" mode="fwd"/>
</xsl:template>



<!--
 * Gör ingenting vid utskrift av LI som inte är först.
 * De har redan skrivits ut.
 -->
<xsl:template match="li">
	<!--<xsl:text>Ignore</xsl:text>-->
</xsl:template>



<!--
 * Skriv ut textnoder.
 -->
<xsl:template match="text()">
	<!--<xsl:text>HEPP</xsl:text>-->
	<xsl:value-of select="."/>
</xsl:template>



<!--
 * Skriv ut allt annat som det är. (Borde vara övriga element)
 * Skriv även ut eventuella attribut.
 -->
<xsl:template match="*">
  <xsl:element name="{name()}">
  	<xsl:for-each select="@*">
  		<xsl:attribute name="{name()}">
  			<xsl:value-of select="."/>
  		</xsl:attribute>
  	</xsl:for-each>
  	<xsl:apply-templates/>
    <!--<xsl:copy-of select="node()"/>-->
  </xsl:element>
</xsl:template>



</xsl:stylesheet>
