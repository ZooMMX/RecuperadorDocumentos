SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP SCHEMA IF EXISTS `recuperacion` ;
CREATE SCHEMA IF NOT EXISTS `recuperacion` DEFAULT CHARACTER SET latin1 COLLATE latin1_spanish_ci ;
USE `recuperacion` ;

-- -----------------------------------------------------
-- Table `recuperacion`.`Documento`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `recuperacion`.`Documento` (
  `id` INT NOT NULL AUTO_INCREMENT ,
  `contenido` TEXT NULL ,
  `lematizado` TEXT NULL ,
  `tipo` ENUM('CONSULTA', 'DOCUMENTO') NOT NULL DEFAULT 'DOCUMENTO' ,
  `ruta` TEXT NULL ,
  PRIMARY KEY (`id`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `recuperacion`.`Termino`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `recuperacion`.`Termino` (
  `nombre` VARCHAR(32) NOT NULL ,
  PRIMARY KEY (`nombre`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `recuperacion`.`Tiene`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `recuperacion`.`Tiene` (
  `Documento_id` INT NOT NULL ,
  `termino` VARCHAR(32) NOT NULL ,
  `frecuencia` INT NULL ,
  PRIMARY KEY (`Documento_id`, `termino`) ,
  INDEX `fk_Tiene_Termino` (`termino` ASC) ,
  INDEX `fk_Tiene_Documento1` (`Documento_id` ASC) ,
  CONSTRAINT `fk_Tiene_Termino`
    FOREIGN KEY (`termino` )
    REFERENCES `recuperacion`.`Termino` (`nombre` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Tiene_Documento1`
    FOREIGN KEY (`Documento_id` )
    REFERENCES `recuperacion`.`Documento` (`id` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `recuperacion`.`MatrizSVD`
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS `recuperacion`.`MatrizSVD` (
  `indice` ENUM('mayor','optimo','menor') NOT NULL ,
  `u` MEDIUMBLOB NOT NULL ,
  `s` MEDIUMBLOB NOT NULL ,
  `v` MEDIUMBLOB NOT NULL ,
  `k` INT NOT NULL ,
  PRIMARY KEY (`indice`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Placeholder table for view `recuperacion`.`completa`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `recuperacion`.`completa` (`termino` INT, `frecuencia` INT);

-- -----------------------------------------------------
-- View `recuperacion`.`completa`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `recuperacion`.`completa`;
USE `recuperacion`;
CREATE  OR REPLACE VIEW `recuperacion`.`completa` AS
  SELECT 
    d.*,
    ti.termino, ti.frecuencia
  FROM
    documento as d,
    tiene as ti
  WHERE
      d.id = ti.documento_id
  ORDER BY 
    ti.termino asc

;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
